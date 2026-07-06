package infinitygroup.thecamarsenal.aim;

import infinitygroup.thecamarsenal.TheCamArsenal;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public final class TheCamCompat {
    private static final String[] MOD_IDS = {"thecam", "the_cam"};
    private static final String API_CLASS = "thecam.api.TheCamAimApi";
    private static final String ORIGIN_METHOD = "getAimOrigin";
    private static final String DIRECTION_METHOD = "getAimDirection";
    private static final String TARGET_METHOD = "getAimTarget";
    private static final String ACTIVE_METHOD = "isTheCamAimActive";
    private static final String ACTIVE_ALIAS_METHOD = "isAimActive";
    private static final String HAS_TARGET_METHOD = "hasAimTarget";

    private TheCamCompat() {
    }

    public static boolean isInstalled() {
        for (String modId : MOD_IDS) {
            if (ModList.get().isLoaded(modId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApiAvailable() {
        return loadClass(API_CLASS) != null;
    }

    public static boolean isAimActive(Player player) {
        Class<?> api = loadClass(API_CLASS);
        if (api == null) {
            return false;
        }
        try {
            Boolean active = invokeBoolean(api, ACTIVE_METHOD, player);
            if (active == null) {
                active = invokeBoolean(api, ACTIVE_ALIAS_METHOD, player);
            }
            return active != null && active.booleanValue();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public static boolean hasAimTarget(Player player) {
        Class<?> api = loadClass(API_CLASS);
        if (api == null) {
            return false;
        }
        try {
            Boolean value = invokeBoolean(api, HAS_TARGET_METHOD, player);
            return value != null && value.booleanValue();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public static Vec3 getAimOrigin(Player player) {
        Class<?> api = loadClass(API_CLASS);
        if (api != null) {
            try {
                Vec3 origin = invokeVec3(api, ORIGIN_METHOD, player);
                if (origin != null) {
                    return origin;
                }
            } catch (RuntimeException ignored) {
            }
        }
        return player.getEyePosition(1.0F);
    }

    public static Vec3 getAimDirection(Player player) {
        Class<?> api = loadClass(API_CLASS);
        if (api != null) {
            try {
                Vec3 direction = invokeVec3(api, DIRECTION_METHOD, player);
                if (direction != null && direction.lengthSqr() > 1.0E-6D) {
                    return direction.normalize();
                }
            } catch (RuntimeException ignored) {
            }
        }
        return Vec3.directionFromRotation(player.getXRot(), player.getYRot());
    }

    public static Vec3 getAimTarget(Player player) {
        Class<?> api = loadClass(API_CLASS);
        if (api != null) {
            try {
                Vec3 target = invokeVec3(api, TARGET_METHOD, player);
                if (target != null) {
                    return target;
                }
            } catch (RuntimeException ignored) {
            }
        }
        return null;
    }

    public static AimProbe debugProbe(Player player) {
        boolean apiFound = isApiAvailable();
        boolean aimTargetSupported = false;
        boolean aimActive = false;
        boolean hasAimTarget = false;
        Vec3 origin = null;
        Vec3 direction = null;
        Vec3 target = null;
        String error = null;

        try {
            if (apiFound) {
                Class<?> api = loadClass(API_CLASS);
                if (api != null) {
                    aimTargetSupported = hasMethod(api, TARGET_METHOD);
                    origin = invokeVec3(api, ORIGIN_METHOD, player);
                    direction = invokeVec3(api, DIRECTION_METHOD, player);
                    if (aimTargetSupported) {
                        target = invokeVec3(api, TARGET_METHOD, player);
                    }
                    Boolean active = invokeBoolean(api, ACTIVE_METHOD, player);
                    if (active == null) {
                        active = invokeBoolean(api, ACTIVE_ALIAS_METHOD, player);
                    }
                    Boolean targetActive = invokeBoolean(api, HAS_TARGET_METHOD, player);
                    aimActive = active != null && active.booleanValue();
                    hasAimTarget = targetActive != null && targetActive.booleanValue();
                }
            }
        } catch (RuntimeException ex) {
            error = ex.toString();
            TheCamArsenal.LOGGER.warn("The Cam aim reflection error", ex);
        }

        return new AimProbe(apiFound, aimTargetSupported, aimActive, hasAimTarget, origin, direction, target, error);
    }

    private static Vec3 invokeVec3(Class<?> api, String methodName, Player player) {
        Object result = invoke(api, methodName, player);
        return result instanceof Vec3 vec3 ? vec3 : null;
    }

    private static Boolean invokeBoolean(Class<?> api, String methodName, Player player) {
        Object result = invoke(api, methodName, player);
        return result instanceof Boolean value ? value : null;
    }

    private static Object invoke(Class<?> type, String methodName, Player player) {
        try {
            Method method = type.getMethod(methodName, Player.class);
            method.setAccessible(true);
            if (Modifier.isStatic(method.getModifiers())) {
                return method.invoke(null, player);
            }
            return null;
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to invoke " + API_CLASS + "." + methodName, ex);
        }
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    private static boolean hasMethod(Class<?> type, String methodName) {
        try {
            type.getMethod(methodName, Player.class);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }

    public record AimProbe(boolean apiFound, boolean aimTargetSupported, boolean aimActive, boolean hasAimTarget, Vec3 origin, Vec3 direction, Vec3 target, String error) {
    }
}

