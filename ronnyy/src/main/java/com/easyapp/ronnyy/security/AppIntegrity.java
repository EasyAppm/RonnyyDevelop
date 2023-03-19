package com.easyapp.ronnyy.security;

import android.content.Context;

import com.easyapp.core.TypeValidator;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import com.easyapp.util.AssetsUtils;
import com.easyapp.util.ReflectionUtils;
import com.easyapp.util.SignatureUtils;
import android.widget.Toast;

public final class AppIntegrity {

    private final Context context;
    private final Boot[] boots;

    private AppIntegrity(Context context, Boot[] boots) {
        this.context = TypeValidator.argumentNonNull(context, "context cannot be null");
        if (boots == null || boots.length == 0) {
            throw new IllegalArgumentException("boots cannot be null or empty");
        }
        this.boots = boots;
    }

    public static AppIntegrity newInstance(Context context, Boot... boots) {
        return new AppIntegrity(context, boots);
    }

    public Status check() throws Exception {
        Status status = new Status(Status.Type.PASS, "");
        for (Boot boot : boots) {
            if (boot.isAssets()) {
                status = checkAssets(boot.toAssets());
                if (!status.isSafe()) break;
            }
            if (boot.isSuperClass()) {
                status = checkSuperClass(boot.toSuperClass());
                if (!status.isSafe()) break;
            }
            if (boot.isSignature()) {
                status = checkSignature(boot.toSignature());
                if (!status.isSafe()) break;
            }
        }
        return status;
    }

    private Status checkSignature(Signature signature) throws Exception {
        Status status = new Status(Status.Type.PASS, "");
        final String algorithm = signature.getAlgorithm();
        final SignatureUtils.Result result;
        switch (signature.getMode()) {
            case APP:
                result = SignatureUtils.fromApp(context, algorithm);
                if (result.isSuccess()) {
                    if(!signature.isHashOriginal(result.getData())){
                        status = new Status(
                            Status.Type.VIOLATED_SIGNATURE,
                            "App signature is not the same as expected"
                        );
                    }
                }else throw result.getError();
                break;
            case PATH:
                result = SignatureUtils.fromPath(context, signature.hashSource, algorithm);
                if (result.isSuccess()) {
                    if(!signature.isHashOriginal(result.getData())){
                        byte[] origin = result.getData();
                        byte[] fora = signature.hashExpected;
                        Toast.makeText(context, String.valueOf(origin == fora), 1).show();
                        Thread.sleep(2000);
                        Toast.makeText(context, "Origin " + Arrays.toString(origin), 1).show();
                        Toast.makeText(context, "fora " + Arrays.toString(fora), 1).show();
                        
                        status = new Status(
                            Status.Type.VIOLATED_SIGNATURE,
                            "Path signature is not the same as expected"
                        );
                    }
                }else throw result.getError();
                break;
            case PACKAGE:
                result = SignatureUtils.fromPackage(context, signature.hashSource, algorithm);
                if (result.isSuccess()) {
                    if(!signature.isHashOriginal(result.getData())){
                        status = new Status(
                            Status.Type.VIOLATED_SIGNATURE,
                            "Package signature is not the same as expected"
                        );
                    }
                }else throw result.getError();
                break;
        }
        return status;
    }

    private Status checkAssets(Assets assets) throws Exception {
        for (File file : AssetsUtils.listAllFiles(context, assets.getDefaultPath())) {
            if (assets.containsInForbidden(file)) {
                return new Status(
                    Status.Type.VIOLATED_ASSETS,
                    "Assets forbidden find : " + file.getAbsolutePath()
                );
            }
        }
        return new Status(Status.Type.PASS, "");
    }

    private Status checkSuperClass(SuperClass superClass) throws Exception {
        Status status = new Status(Status.Type.PASS, "");
        Class<?> entry = null;
        Class<?>[] superClasses = null;
        switch (superClass.getMode()) {
            case LOADED:
                entry = superClass.getClassEntry();
                superClasses = superClass.getSuperClasses();
                break;
            case UNLOADED:
                ClassLoader loader = superClass.getClassLoader();
                if (loader == null) {
                    entry = ReflectionUtils.loadClass(superClass.getClassEntryName());
                    superClasses = ReflectionUtils.loadClasses(superClass.getSuperClassNames());
                } else {
                    entry = ReflectionUtils.loadClass(
                        loader, superClass.getClassEntryName(), false
                    );
                    superClasses = ReflectionUtils.loadClasses(
                        loader, superClass.getSuperClassNames()
                    );
                }
                break;
        }
        for (Class<?> sc : superClasses) {
            if (!ReflectionUtils.isSubClass(entry, sc)) {
                status = new Status(
                    Status.Type.VIOLATED_CLASSES,
                    "The class "
                    + entry.getSimpleName() +
                    " class is not a child of "
                    + sc.getSimpleName()
                );
            }
        }
        return status;
    }

    public static final class Assets extends Boot {
        private String defaultPath = "";
        private final List<String> forbiddenFiles;
        private boolean relativeForbidden;

        private Assets(String... forbiddenFiles) {
            this.forbiddenFiles = Arrays.asList(
                TypeValidator.argumentNonNull(
                    forbiddenFiles,
                    "forbiddenFiles cannot be null"
                )
            );
        }

        public static Assets create(String... forbiddenFiles) {
            return new Assets(forbiddenFiles);
        }

        public Assets setDefaultPath(String defaultPath) {
            if (defaultPath == null) return this;
            this.defaultPath = defaultPath;
            return this;
        }

        public Assets enableRelativeForbidden() {
            relativeForbidden = true;
            return this;
        }

        private boolean containsInForbidden(File file) {
            String path = relativeForbidden ? file.getAbsolutePath() : file.getName();
            return forbiddenFiles.contains(path);
        }

        private String getDefaultPath() {
            return defaultPath;
        }

    }

    public static final class SuperClass extends Boot {
        private final Class<?> classEntry;
        private final Class<?>[] superClasses;
        private final String classEntryName;
        private final String[] superClassNames;
        private final Mode mode;
        private ClassLoader classLoader;

        private SuperClass(Class<?> classEntry, Class<?>... superClasses) {
            this.classEntry = TypeValidator.argumentNonNull(classEntry, "classEntry cannot be null");
            this.superClasses = TypeValidator.argumentNonNull(superClasses, "superClasses cannot be null");
            this.classEntryName = null;
            this.superClassNames = null;
            this.mode = Mode.LOADED;
        }

        private SuperClass(String classEntryName, String... superClassNames) {
            this.classEntryName = TypeValidator.argumentNonNull(classEntryName, "classEntryName cannot be null");
            this.superClassNames = TypeValidator.argumentNonNull(superClassNames, "superClassNames cannot be null");
            this.classEntry = null;
            this.superClasses = null;
            this.mode = Mode.UNLOADED;
        }

        public static SuperClass create(String classEntryName, String... superClassNames) {
            return new SuperClass(classEntryName, superClassNames);
        }

        public static SuperClass create(Class<?> classEntry, Class<?>... superClasses) {
            return new SuperClass(classEntry, superClasses);
        }

        public SuperClass setClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        private Class<?> getClassEntry() {
            return classEntry;
        }

        private Class<?>[] getSuperClasses() {
            return superClasses;
        }

        private String getClassEntryName() {
            return classEntryName;
        }

        private String[] getSuperClassNames() {
            return superClassNames;
        }

        private Mode getMode() {
            return mode;
        }

        private ClassLoader getClassLoader() {
            return classLoader;
        }

        private enum Mode {
            LOADED, UNLOADED
            }

    }

    public static final class Signature extends Boot {

        private static final String DEFAULT_ALGORITHM = "SHA-256";

        private final byte[] hashExpected;
        private final String hashSource;
        private String algorithm;
        private final Mode mode;

        private Signature(String hashSource, byte[] hashExpected, Mode mode) {
            if (mode != Mode.APP) {
                if (hashSource == null) {
                    throw new IllegalArgumentException("hashSource cannot be null");
                } else if (hashSource.isEmpty()) {
                    throw new IllegalArgumentException("hashSource cannot be empty");
                }
            }
            if (hashExpected == null) {
                throw new IllegalArgumentException("hashExpected cannot be null");
            } else if (hashExpected.length == 0) {
                throw new IllegalArgumentException("hashExpected cannot be empty");
            }
            this.hashSource = hashSource;
            this.hashExpected = hashExpected;
            this.mode = mode;
            this.algorithm = DEFAULT_ALGORITHM;
        }

        public static Signature createFromPath(String hashSource, String hashExpected) {
            return createFromPath(hashSource, (hashExpected == null) ? null : resolveHashExpected(hashExpected));
        }

        public static Signature createFromPath(String hashSource, byte[] hashExpected) {
            return createFromPath((hashSource == null) ? null : new File(hashSource), hashExpected);
        }

        public static Signature createFromPath(File hashSource, byte[] hashExpected) {
            return new Signature((hashSource == null) ? null : hashSource.getAbsolutePath(), hashExpected, Mode.PATH);
        }

        public static Signature createFromPackage(String hashSource, String hashExpected) {
            return createFromPackage(hashSource, (hashExpected == null) ? null : resolveHashExpected(hashExpected));
        }

        public static Signature createFromPackage(String hashSource, byte[] hashExpected) {
            return new Signature(hashSource, hashExpected, Mode.PACKAGE);
        }

        public static Signature createFromApp(String hashExpected) {
            return createFromApp((hashExpected == null) ? null : resolveHashExpected(hashExpected));
        }

        public static Signature createFromApp(byte[] hashExpected) {
            return new Signature(null, hashExpected, Mode.APP);
        }

        public Signature setAlgorithm(String algorithm) {
            this.algorithm = (algorithm == null) ? DEFAULT_ALGORITHM : algorithm;
            return this;
        }

        private boolean isHashOriginal(byte[] bytes){
            
            return bytes != null && Arrays.equals(bytes, hashExpected);
        }

        private String getAlgorithm() {
            return algorithm;
        }

        private Mode getMode() {
            return mode;
        }
        
        private static byte[] resolveHashExpected(String hashSource){
            if(!hashSource.contains(":")){
                throw new IllegalArgumentException("HashSource must be separate for ':'");
            }
            String[] hexValues = hashSource.split(":");
            byte[] bytes = new byte[hexValues.length];
            int pos = 0;
            for(String hex : hexValues){
                bytes[pos++] = (byte)Integer.parseInt(hex.trim(), 16);
            }
            return bytes;
        }

        private enum Mode {
            PACKAGE, APP, PATH
            }

    }

    public static abstract class Boot {

        private boolean isAssets() {
            return this instanceof Assets;
        }

        private boolean isSuperClass() {
            return this instanceof SuperClass;
        }

        private boolean isSignature() {
            return this instanceof Signature;
        }

        private Assets toAssets() {
            if (isAssets()) {
                return (Assets) this;
            }
            throw new IllegalStateException("Boot cannot convert to Assets");
        }

        private SuperClass toSuperClass() {
            if (isSuperClass()) {
                return (SuperClass) this;
            }
            throw new IllegalStateException("Boot cannot convert to SuperClass");
        }

        private Signature toSignature() {
            if (isSignature()) {
                return (Signature) this;
            }
            throw new IllegalStateException("Boot cannot convert to Signature");
        }

    }

    public final static class Status {

        private final Type type;
        private final String about;

        public Status(Type type, String about) {
            this.type = type;
            this.about = about;
        }

        public Type getType() {
            return type;
        }

        public String getAbout() {
            return about;
        }

        public boolean isSafe() {
            return type == Type.PASS;
        }

        public enum Type {
            VIOLATED_ASSETS, VIOLATED_CLASSES, VIOLATED_SIGNATURE, PASS
            }

    }

}
