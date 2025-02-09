package person.pluto.natcross2.serverside.client.handler;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.common.Optional;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;
import person.pluto.natcross2.model.enumeration.NatcrossResultEnum;
import person.pluto.natcross2.serverside.client.adapter.PassValueNextEnum;
import person.pluto.natcross2.serverside.client.process.IProcess;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 常规接收处理handler
 * </p>
 *
 * @author Pluto
 * @since 2020-01-06 10:20:11
 */
@Slf4j
public class InteractiveProcessHandler implements IPassValueHandler<InteractiveModel, InteractiveModel> {

    /**
     * 处理链表
     */
    private List<IProcess> processList = new LinkedList<>();

    @Override
    public PassValueNextEnum proc(SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel,
            Optional<? extends InteractiveModel> optional) {
        InteractiveModel value = optional.getValue();
        log.info("接收到新消息：[ {} ]", value);

        for (IProcess process : this.processList) {
            boolean wouldProc = process.wouldProc(value);
            if (wouldProc) {
                boolean processMethod;
                try {
                    processMethod = process.processMethod(socketChannel, value);
                } catch (Exception e) {
                    log.error("处理任务时发生异常", e);
                    return PassValueNextEnum.STOP_CLOSE;
                }
                if (processMethod) {
                    return PassValueNextEnum.STOP_KEEP;
                } else {
                    return PassValueNextEnum.STOP_CLOSE;
                }
            }
        }

        try {
            socketChannel.writeAndFlush(InteractiveModel.of(value.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
                    NatcrossResultEnum.UNKNOWN_INTERACTIVE_TYPE.toResultModel()));
        } catch (Exception e) {
            log.error("发送消息时异常", e);
        }

        return PassValueNextEnum.STOP_CLOSE;
    }

    /**
     * 将处理方法加入后面
     *
     * @param process
     * @return
     */
    public InteractiveProcessHandler addLast(IProcess process) {
        this.processList.add(process);
        return this;
    }

    /**
     * 获取处理链表，可以代理维护
     *
     * @return
     */
    public List<IProcess> getProcessList() {
        return this.processList;
    }

    /**
     * 设置处理链表
     *
     * @param processList
     * @return
     */
    public InteractiveProcessHandler setProcessList(List<IProcess> processList) {
        this.processList = processList;
        return this;
    }

}
