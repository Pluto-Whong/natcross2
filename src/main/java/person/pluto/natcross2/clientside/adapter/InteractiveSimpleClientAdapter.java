package person.pluto.natcross2.clientside.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.config.IClientConfig;
import person.pluto.natcross2.clientside.handler.IClientHandler;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.NatcrossResultModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;
import person.pluto.natcross2.model.enumeration.NatcrossResultEnum;
import person.pluto.natcross2.model.interactive.ClientControlModel;
import person.pluto.natcross2.model.interactive.ServerWaitModel;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 基于InteractiveModel的客户端适配器
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:24:07
 */
@Slf4j
public class InteractiveSimpleClientAdapter implements IClientAdapter<InteractiveModel, InteractiveModel> {

    /**
     * 所属的客户端线程
     */
    private final ClientControlThread clientControlThread;
    /**
     * 客户端设置
     */
    private final IClientConfig<InteractiveModel, InteractiveModel> config;

    /**
     * 适配器通道
     */
    private SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel;

    private LocalDateTime serverHeartLastRecvTime = LocalDateTime.now();

    /**
     * 客户端消息接收处理链
     */
    protected List<IClientHandler<? super InteractiveModel, ? extends InteractiveModel>> messageHandlerList = new LinkedList<>();

    public InteractiveSimpleClientAdapter(ClientControlThread clientControlThread,
                                          IClientConfig<InteractiveModel, InteractiveModel> clientConfig) {
        this.clientControlThread = clientControlThread;
        this.config = clientConfig;
    }

    /**
     * 创建客户端通道
     *
     * @return
     */
    protected SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
        return this.config.newClientChannel();
    }

    /**
     * 向穿透目标socket
     *
     * @return
     * @throws Exception
     */
    protected Socket newDestSocket() throws Exception {
        return this.config.newDestSocket();
    }

    /**
     * 向服务端和暴露目标创建socketPart
     *
     * @return
     */
    protected AbsSocketPart newSocketPart() {
        return this.config.newSocketPart(this.clientControlThread);
    }

    @Override
    public boolean createControlChannel() throws Exception {
        SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel = this.newClientChannel();
        if (socketChannel == null) {
            log.error("向服务端[{}:{}]建立控制通道失败", this.config.getClientServiceIp(),
                    this.config.getClientServicePort());
            return false;
        }

        InteractiveModel interactiveModel = InteractiveModel.of(InteractiveTypeEnum.CLIENT_CONTROL,
                new ClientControlModel(this.config.getListenServerPort()));

        socketChannel.writeAndFlush(interactiveModel);

        InteractiveModel recv = socketChannel.read();
        log.info("建立控制端口回复：{}", recv);

        NatcrossResultModel javaObject = recv.getData().toJavaObject(NatcrossResultModel.class);

        if (StringUtils.equals(NatcrossResultEnum.SUCCESS.getCode(), javaObject.getRetCod())) {
            // 使用相同的
            this.socketChannel = socketChannel;

            this.resetServerHeartLastRecvTime();

            return true;
        }
        return false;
    }

    /**
     * 建立连接
     *
     * @param serverWaitModel
     */
    @Override
    public boolean clientConnect(ServerWaitModel serverWaitModel) {
        // 首先向暴露目标建立socket
        Socket destSocket;
        try {
            destSocket = this.newDestSocket();
        } catch (Exception e) {
            log.error("向目标建立连接失败 {}:{}", this.config.getDestIp(), this.config.getDestPort());
            return false;
        }

        SocketChannel<? extends InteractiveModel, ? super InteractiveModel> passwayClientChannel = null;
        try {
            // 向服务端请求建立隧道
            passwayClientChannel = this.newClientChannel();

            InteractiveModel model = InteractiveModel.of(InteractiveTypeEnum.CLIENT_CONNECT,
                    new ServerWaitModel(serverWaitModel.getSocketPartKey()));

            passwayClientChannel.writeAndFlush(model);

            InteractiveModel recv = passwayClientChannel.read();
            log.info("建立隧道回复：{}", recv);

            NatcrossResultModel javaObject = recv.getData().toJavaObject(NatcrossResultModel.class);

            if (!StringUtils.equals(NatcrossResultEnum.SUCCESS.getCode(), javaObject.getRetCod())) {
                throw new RuntimeException("绑定失败");
            }

        } catch (Exception e) {
            log.error(
                    "打通隧道发生异常 " + this.config.getClientServiceIp() + ":" + this.config.getClientServicePort() +
                            "<->" + this.config.getDestIp() + ":" + this.config.getDestPort(), e);
            try {
                destSocket.close();
            } catch (IOException e1) {
                // do nothing
            }

            if (passwayClientChannel != null) {
                try {
                    passwayClientChannel.closeSocket();
                } catch (IOException e1) {
                    // do nothing
                }
            }
            return false;
        }

        // 将两个socket建立伙伴关系
        AbsSocketPart socketPart = this.newSocketPart();
        socketPart.setSocketPartKey(serverWaitModel.getSocketPartKey());
        socketPart.setSendSocket(passwayClientChannel.getSocket());
        socketPart.setRecvSocket(destSocket);
        // 尝试打通隧道
        boolean createPassWay = socketPart.createPassWay();
        if (!createPassWay) {
            socketPart.cancel();
            return false;
        }

        // 将socket伙伴放入客户端线程进行统一管理
        this.clientControlThread.putSocketPart(serverWaitModel.getSocketPartKey(), socketPart);
        return true;
    }

    @Override
    public void waitMessage() throws Exception {
        InteractiveModel read = this.socketChannel.read();

        // 只要有有效信息推送过来，则重置心跳时间
        this.resetServerHeartLastRecvTime();

        NatcrossExecutor.executeClientMessageProc(() -> this.procMethod(read));
    }

    /**
     * 消息处理方法
     *
     * @param recvInteractiveModel
     */
    protected void procMethod(InteractiveModel recvInteractiveModel) {
        log.info("接收到新的指令: {}", recvInteractiveModel);
        try {
            boolean proceedFlag = false;

            for (IClientHandler<? super InteractiveModel, ? extends InteractiveModel> handler : this.messageHandlerList) {
                proceedFlag = handler.proc(recvInteractiveModel, this);
                if (proceedFlag) {
                    break;
                }
            }

            if (!proceedFlag) {
                log.warn("无处理方法的信息：[{}]", recvInteractiveModel);
                InteractiveModel result = InteractiveModel.of(recvInteractiveModel.getInteractiveSeq(),
                        InteractiveTypeEnum.COMMON_REPLY, NatcrossResultEnum.UNKNOWN_INTERACTIVE_TYPE.toResultModel());
                this.getSocketChannel().writeAndFlush(result);
            }

        } catch (Exception e) {
            // 只记录，其他的交给心跳之类的去把控
            log.error("读取或写入异常", e);
        }
    }

    @Override
    public SocketChannel<? extends InteractiveModel, ? super InteractiveModel> getSocketChannel() {
        return this.socketChannel;
    }

    /**
     * 添加消息处理器
     *
     * @param handler
     * @return
     */
    public InteractiveSimpleClientAdapter addMessageHandler(
            IClientHandler<? super InteractiveModel, ? extends InteractiveModel> handler) {
        this.messageHandlerList.add(handler);
        return this;
    }

    @Override
    public void close() throws Exception {
        SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel = this.socketChannel;
        if (Objects.nonNull(socketChannel)) {
            this.socketChannel = null;
            socketChannel.closeSocket();
        }
    }

    @Override
    public void sendHeartTest() throws Exception {
        InteractiveModel interactiveModel = InteractiveModel.of(InteractiveTypeEnum.HEART_TEST, null);
        this.socketChannel.writeAndFlush(interactiveModel);
    }

    @Override
    public LocalDateTime obtainServerHeartLastRecvTime() {
        return this.serverHeartLastRecvTime;
    }

    @Override
    public LocalDateTime resetServerHeartLastRecvTime(LocalDateTime time) {
        LocalDateTime tempTime = this.serverHeartLastRecvTime;
        this.serverHeartLastRecvTime = time;
        return tempTime;
    }

}
