package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.svm.JNIEnv.JClass;
import org.apidesign.demo.matrixultimate.svm.JNIEnv.JFieldID;
import org.apidesign.demo.matrixultimate.svm.JNIEnv.JObject;
import org.graalvm.nativeimage.c.type.CTypeConversion;

final class Copy {
    static JObject deepCopy(String objClassName, JObject fromObj, JNIEnv fromEnv, JNIEnv toEnv) {
        final JNIEnv.JNINativeInterface fromFn = fromEnv.getFunctions();
        final JNIEnv.JNINativeInterface toFn = toEnv.getFunctions();
        System.err.println("deepCopy");
        try (
            CTypeConversion.CCharPointerHolder strObjClassName = CTypeConversion.toCString(objClassName);
        ) {
            System.err.println("name done");
//            JClass fromObjClass = fromFn.getGetObjectClass().getClass(fromEnv, from);
            JClass fromObjClass = fromFn.getFindClass().find(fromEnv, strObjClassName.get());
            System.err.println("class of obj: " + fromObjClass.rawValue());
            JClass toObjClass = toFn.getFindClass().find(toEnv, strObjClassName.get());
            System.err.println("classes: " + toObjClass.rawValue());

            JObject toObj = toFn.getAllocObject().alloc(toEnv, toObjClass);
            System.err.println("newObj: " + toObj.rawValue());

            copyLongField("row", fromEnv, fromObjClass, fromObj, toEnv, toObjClass, toObj);
            System.err.println("one");
            copyLongField("column", fromEnv, fromObjClass, fromObj, toEnv, toObjClass, toObj);
            System.err.println("two");
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

            System.err.println("objFieldId: " + fromFieldId.rawValue());
            System.err.println("toFieldId: " + toFieldId.rawValue());
            if (fromFieldId.isNull()) {
                throw new IllegalStateException("from field is null: " + fieldName);
            }
            if (toFieldId.isNull()) {
                throw new IllegalStateException("to field is null: " + fieldName);
            }

            long zero = toFn.getGetLongField().get(toEnv, toObj, toFieldId);
            System.err.println("zero: " + zero);
            long value = fromFn.getGetLongField().get(fromEnv, fromObj, fromFieldId);

            System.err.println("value: " + value);
            toFn.getSetLongField().set(toEnv, toObj, toFieldId, value);
        }
    }
}
