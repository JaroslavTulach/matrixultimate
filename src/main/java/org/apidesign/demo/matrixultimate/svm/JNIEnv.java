package org.apidesign.demo.matrixultimate.svm;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CPointerTo;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

@CContext(JNIHeaderDirectives.class)
@CStruct(value = "JNIEnv_", addStructKeyword = true)
interface JNIEnv extends PointerBase {
    @CField("functions")
    JNINativeInterface getFunctions();

    @CPointerTo(JNIEnv.class)
    interface JNIEnvironmentPointer extends PointerBase {
        JNIEnv read();
        void write(JNIEnv value);
    }

    @CStruct(value = "JNINativeInterface_", addStructKeyword = true)
    interface JNINativeInterface extends PointerBase {
        @CField
        GetMethodId getGetMethodID();

        @CField
        GetFieldId getGetFieldID();

        @CField
        FindClass getFindClass();

        @CField
        GetObjectClass getGetObjectClass();

        @CField
        CallStaticVoidMethod getCallStaticVoidMethodA();

        @CField
        CallStaticObjectMethod getNewObjectA();

        @CField
        AllocObject getAllocObject();

        @CField
        GetLongField getGetLongField();

        @CField
        SetLongField getSetLongField();
    }

    interface GetMethodId extends CFunctionPointer {
        @InvokeCFunctionPointer
        JMethodID find(JNIEnv env, JClass clazz, CCharPointer name, CCharPointer sig);
    }

    interface GetFieldId extends CFunctionPointer {
        @InvokeCFunctionPointer
        JFieldID find(JNIEnv env, JClass clazz, CCharPointer name, CCharPointer sig);
    }

    interface FindClass extends CFunctionPointer {
        @InvokeCFunctionPointer
        JClass find(JNIEnv env, CCharPointer name);
    }

    interface GetObjectClass extends CFunctionPointer {
        @InvokeCFunctionPointer
        JClass getClass(JNIEnv env, JObject obj);
    }

    interface AllocObject extends CFunctionPointer {
        @InvokeCFunctionPointer
        JObject alloc(JNIEnv env, JClass clazz);
    }

    interface GetLongField extends CFunctionPointer {
        @InvokeCFunctionPointer
        long get(JNIEnv env, JObject obj, JFieldID fieldID);
    }

    interface SetLongField extends CFunctionPointer {
        @InvokeCFunctionPointer
        void set(JNIEnv env, JObject obj, JFieldID fieldID, long value);
    }

    interface JObject extends PointerBase {
    }

    interface CallStaticVoidMethod extends CFunctionPointer {
        @InvokeCFunctionPointer
        void call(JNIEnv env, JClass cls, JMethodID methodid, JValue args);
    }

    interface CallStaticObjectMethod extends CFunctionPointer {
        @InvokeCFunctionPointer
        JObject call(JNIEnv env, JClass cls, JMethodID methodid, JValue args);
    }

    interface JClass extends PointerBase {
    }
    interface JMethodID extends PointerBase {
    }
    interface JFieldID extends PointerBase {
    }

    @CStruct("jvalue")
    interface JValue extends PointerBase {
        @CField boolean z();
        @CField byte b();
        @CField char c();
        @CField short s();
        @CField int i();
        @CField long j();
        @CField float f();
        @CField double d();
        @CField JObject l();


        @CField void z(boolean b);
        @CField void b(byte b);
        @CField void c(char ch);
        @CField void s(short s);
        @CField void i(int i);
        @CField void j(long l);
        @CField void f(float f);
        @CField void d(double d);
        @CField void l(JObject obj);

        JValue addressOf(int index);
    }

}

final class JNIHeaderDirectives implements CContext.Directives {
    @Override
    public List<String> getOptions() {
        File[] jnis = findJNIHeaders();
        return Arrays.asList("-I" + jnis[0].getParent(), "-I" + jnis[1].getParent());
    }

    @Override
    public List<String> getHeaderFiles() {
        File[] jnis = findJNIHeaders();
        return Arrays.asList("<" + jnis[0] + ">", "<" + jnis[1] + ">");
    }

    private static File[] findJNIHeaders() throws IllegalStateException {
        final File jreHome = new File(System.getProperty("java.home"));
        final File include = new File(jreHome.getParentFile(), "include");
        final File[] jnis = {
            new File(include, "jni.h"),
            null,
        };

        for (File f : include.listFiles()) {
            if (f.isDirectory()) {
                File jni_md = new File(f, "jni_md.h");
                if (jni_md.exists()) {
                    jnis[1] = jni_md;
                    break;
                }
            }
        }

        if (jnis[1] == null) {
            throw new IllegalStateException("Cannot find jni_md.h under " + include);
        }

        return jnis;
    }
}
