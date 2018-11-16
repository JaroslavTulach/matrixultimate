package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.svm.JNIEnv.JClass;
import org.apidesign.demo.matrixultimate.svm.JNIEnv.JFieldID;
import org.apidesign.demo.matrixultimate.svm.JNIEnv.JObject;
import org.graalvm.nativeimage.c.type.CTypeConversion;

final class Copy {
    static JObject deepCopy(String objClassName, JObject fromObj, JNIEnv fromEnv, JNIEnv toEnv) {
        final JNIEnv.JNINativeInterface fromFn = fromEnv.getFunctions();
        final JNIEnv.JNINativeInterface toFn = toEnv.getFunctions();
        try (
            CTypeConversion.CCharPointerHolder strObjClassName = CTypeConversion.toCString(objClassName);
        ) {
//            JClass fromObjClass = fromFn.getGetObjectClass().getClass(fromEnv, from);
            JClass fromObjClass = fromFn.getFindClass().find(fromEnv, strObjClassName.get());
            JClass toObjClass = toFn.getFindClass().find(toEnv, strObjClassName.get());

            JObject toObj = toFn.getAllocObject().alloc(toEnv, toObjClass);

            copyLongField("row", fromEnv, fromObjClass, fromObj, toEnv, toObjClass, toObj);
            copyLongField("column", fromEnv, fromObjClass, fromObj, toEnv, toObjClass, toObj);
            copyLongField("size", fromEnv, fromObjClass, fromObj, toEnv, toObjClass, toObj);
            copyLongField("time", fromEnv, fromObjClass, fromObj, toEnv, toObjClass, toObj);

            return toObj;
        }
    }

    private static void copyLongField(
        final String fieldName,
        JNIEnv fromEnv, JClass fromObjClass, JObject fromObj,
        JNIEnv toEnv, JClass toObjClass, JObject toObj
    ) {
        try (
            CTypeConversion.CCharPointerHolder rowName = CTypeConversion.toCString(fieldName);
            CTypeConversion.CCharPointerHolder longSig = CTypeConversion.toCString("J");
        ) {
            final JNIEnv.JNINativeInterface fromFn = fromEnv.getFunctions();
            final JNIEnv.JNINativeInterface toFn = toEnv.getFunctions();

            JFieldID fromFieldId = fromFn.getGetFieldID().find(fromEnv, fromObjClass, rowName.get(), longSig.get());
            JFieldID toFieldId = toFn.getGetFieldID().find(toEnv, toObjClass, rowName.get(), longSig.get());

            if (fromFieldId.isNull()) {
                throw new IllegalStateException("from field is null: " + fieldName);
            }
            if (toFieldId.isNull()) {
                throw new IllegalStateException("to field is null: " + fieldName);
            }

            long value = fromFn.getGetLongField().get(fromEnv, fromObj, fromFieldId);
            toFn.getSetLongField().set(toEnv, toObj, toFieldId, value);
        }
    }
}
