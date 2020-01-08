package person.pluto.natcross2.serverside.client.adapter;

/**
 * 
 * <p>
 * 传值适配器的handler回复信息
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:40:54
 */
public enum PassValueNextEnum {

    // 停止并关闭
    STOP_CLOSE(false, true),
    // 停止但不关闭
    STOP_KEEP(false, false),
    // 继续执行，默认关闭
    NEXT(true, true),
    // 继续执行，但不要关闭
    NEXT_KEEP(true, false),
    //
    ;

    private boolean nextFlag;
    private boolean closeFlag;

    private PassValueNextEnum(boolean nextFlag, boolean closeFlag) {
        this.nextFlag = nextFlag;
        this.closeFlag = closeFlag;
    }

    public boolean isNextFlag() {
        return nextFlag;
    }

    public boolean isCloseFlag() {
        return closeFlag;
    }

}
