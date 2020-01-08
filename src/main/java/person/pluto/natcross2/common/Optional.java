package person.pluto.natcross2.common;

/**
 * 
 * <p>
 * 操作对象，jdk8的不带重设功能，但是我们需要，所以进行了类似的重写
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:35:46
 * @param <T> 存放的类型
 */
public class Optional<T> {

    public static <T> Optional<T> of(T value) {
        return new Optional<T>(value);
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
