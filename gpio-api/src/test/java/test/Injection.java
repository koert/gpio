package test;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author Koert Zeilstra
 */
public class Injection {

    /**
     * Inject injected in target with fieldName.
     * @param injected Inject this object.
     * @param target Inject injected into this target.
     * @param fieldName Name of field.
     */
    public static void injectInto(final Object injected, final Object target, final String fieldName) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, injected);
    }

}
