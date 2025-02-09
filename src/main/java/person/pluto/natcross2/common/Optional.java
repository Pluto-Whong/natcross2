package person.pluto.natcross2.common;

/**
 * <p>
 * 操作对象，主要是让值能够通过引用进行传递
 * </p>
 *
 * @param <T> 存放的类型
 * @author Pluto
 * @since 2020-01-08 16:35:46
 */
public class Optional<T> {

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public Optional(T value) {
        this.value = value;
    }

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
