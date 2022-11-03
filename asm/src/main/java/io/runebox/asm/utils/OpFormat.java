package io.runebox.asm.utils;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class OpFormat implements Opcodes {
  private OpFormat() {
  }

  public static Map<Integer, String> accessCodes = getAccessCodes();
  public static Map<Integer, String> typeCodes = getTypeCodes();
  public static Map<Integer, String> handleCodes = getHandleCodes();
  public static Map<Integer, String> frameTypes = getFrameTypes();

  public static Map<Integer, String> opCodes = getCodes();
  public static Map<String, Integer> reCodes = getReCodes();

  private static Map<Integer, String> getAccessCodes() {
    HashMap<Integer, String> map = new HashMap<>();
    map.put(-1, "INVALID ACCESS CODE");
    map.put(ACC_PUBLIC, "ACC_PUBLIC");
    map.put(ACC_PRIVATE, "ACC_PRIVATE");
    map.put(ACC_PROTECTED, "ACC_PROTECTED");
    map.put(ACC_STATIC, "ACC_STATIC");
    map.put(ACC_FINAL, "ACC_FINAL");
    map.put(ACC_SUPER, "ACC_SUPER");
    map.put(ACC_SYNCHRONIZED, "ACC_SYNCHRONIZED");
    map.put(ACC_VOLATILE, "ACC_VOLATILE");
    map.put(ACC_BRIDGE, "ACC_BRIDGE");
    map.put(ACC_VARARGS, "ACC_VARARGS");
    map.put(ACC_TRANSIENT, "ACC_TRANSIENT");
    map.put(ACC_NATIVE, "ACC_NATIVE");
    map.put(ACC_INTERFACE, "ACC_INTERFACE");
    map.put(ACC_ABSTRACT, "ACC_ABSTRACT");
    map.put(ACC_STRICT, "ACC_STRICT");
    map.put(ACC_SYNTHETIC, "ACC_SYNTHETIC");
    map.put(ACC_ANNOTATION, "ACC_ANNOTATION");
    map.put(ACC_ENUM, "ACC_ENUM");
    map.put(ACC_MANDATED, "ACC_MANDATED");
    map.put(ACC_DEPRECATED, "ACC_DEPRECATED");
    return map;
  }

  private static Map<Integer, String> getTypeCodes() {
    HashMap<Integer, String> map = new HashMap<>();
    map.put(-1, "INVALID TYPE CODE");
    map.put(T_BOOLEAN, "T_BOOLEAN");
    map.put(T_CHAR, "T_CHAR");
    map.put(T_FLOAT, "T_FLOAT");
    map.put(T_DOUBLE, "T_DOUBLE");
    map.put(T_BYTE, "T_BYTE");
    map.put(T_SHORT, "T_SHORT");
    map.put(T_INT, "T_INT");
    map.put(T_LONG, "T_LONG");
    return map;
  }

  private static Map<Integer, String> getHandleCodes() {
    HashMap<Integer, String> map = new HashMap<>();
    map.put(-1, "INVALID HANDLE OPCODE");
    map.put(H_GETFIELD, "H_GETFIELD");
    map.put(H_GETSTATIC, "H_GETSTATIC");
    map.put(H_PUTFIELD, "H_PUTFIELD");
    map.put(H_PUTSTATIC, "H_PUTSTATIC");
    map.put(H_INVOKEVIRTUAL, "H_INVOKEVIRTUAL");
    map.put(H_INVOKESTATIC, "H_INVOKESTATIC");
    map.put(H_INVOKESPECIAL, "H_INVOKESPECIAL");
    map.put(H_NEWINVOKESPECIAL, "H_NEWINVOKESPECIAL");
    map.put(H_INVOKEINTERFACE, "H_INVOKEINTERFACE");
    return map;
  }

  private static Map<Integer, String> getFrameTypes() {
    HashMap<Integer, String> map = new HashMap<>();
    map.put(-1, "INVALID OPCODE");
    map.put(F_NEW, "F_NEW");
    map.put(F_FULL, "F_FULL");
    map.put(F_APPEND, "F_APPEND");
    map.put(F_CHOP, "F_CHOP");
    map.put(F_SAME, "F_SAME");
    map.put(F_SAME1, "F_SAME1");
    return map;
  }

  private static Map<Integer, String> getCodes() {
    HashMap<Integer, String> map = new HashMap<>();
    map.put(-1, "INVALID OPCODE");
    map.put(NOP, "NOP");
    map.put(ACONST_NULL, "ACONST_NULL");
    map.put(ICONST_M1, "ICONST_M1");
    map.put(ICONST_0, "ICONST_0");
    map.put(ICONST_1, "ICONST_1");
    map.put(ICONST_2, "ICONST_2");
    map.put(ICONST_3, "ICONST_3");
    map.put(ICONST_4, "ICONST_4");
    map.put(ICONST_5, "ICONST_5");
    map.put(LCONST_0, "LCONST_0");
    map.put(LCONST_1, "LCONST_1");
    map.put(FCONST_0, "FCONST_0");
    map.put(FCONST_1, "FCONST_1");
    map.put(FCONST_2, "FCONST_2");
    map.put(DCONST_0, "DCONST_0");
    map.put(DCONST_1, "DCONST_1");
    map.put(BIPUSH, "BIPUSH");
    map.put(SIPUSH, "SIPUSH");
    map.put(LDC, "LDC");
    map.put(ILOAD, "ILOAD");
    map.put(LLOAD, "LLOAD");
    map.put(FLOAD, "FLOAD");
    map.put(DLOAD, "DLOAD");
    map.put(ALOAD, "ALOAD");
    map.put(IALOAD, "IALOAD");
    map.put(LALOAD, "LALOAD");
    map.put(FALOAD, "FALOAD");
    map.put(DALOAD, "DALOAD");
    map.put(AALOAD, "AALOAD");
    map.put(BALOAD, "BALOAD");
    map.put(CALOAD, "CALOAD");
    map.put(SALOAD, "SALOAD");
    map.put(ISTORE, "ISTORE");
    map.put(LSTORE, "LSTORE");
    map.put(FSTORE, "FSTORE");
    map.put(DSTORE, "DSTORE");
    map.put(ASTORE, "ASTORE");
    map.put(IASTORE, "IASTORE");
    map.put(LASTORE, "LASTORE");
    map.put(FASTORE, "FASTORE");
    map.put(DASTORE, "DASTORE");
    map.put(AASTORE, "AASTORE");
    map.put(BASTORE, "BASTORE");
    map.put(CASTORE, "CASTORE");
    map.put(SASTORE, "SASTORE");
    map.put(POP, "POP");
    map.put(POP2, "POP2");
    map.put(DUP, "DUP");
    map.put(DUP_X1, "DUP_X1");
    map.put(DUP_X2, "DUP_X2");
    map.put(DUP2, "DUP2");
    map.put(DUP2_X1, "DUP2_X1");
    map.put(DUP2_X2, "DUP2_X2");
    map.put(SWAP, "SWAP");
    map.put(IADD, "IADD");
    map.put(LADD, "LADD");
    map.put(FADD, "FADD");
    map.put(DADD, "DADD");
    map.put(ISUB, "ISUB");
    map.put(LSUB, "LSUB");
    map.put(FSUB, "FSUB");
    map.put(DSUB, "DSUB");
    map.put(IMUL, "IMUL");
    map.put(LMUL, "LMUL");
    map.put(FMUL, "FMUL");
    map.put(DMUL, "DMUL");
    map.put(IDIV, "IDIV");
    map.put(LDIV, "LDIV");
    map.put(FDIV, "FDIV");
    map.put(DDIV, "DDIV");
    map.put(IREM, "IREM");
    map.put(LREM, "LREM");
    map.put(FREM, "FREM");
    map.put(DREM, "DREM");
    map.put(INEG, "INEG");
    map.put(LNEG, "LNEG");
    map.put(FNEG, "FNEG");
    map.put(DNEG, "DNEG");
    map.put(ISHL, "ISHL");
    map.put(LSHL, "LSHL");
    map.put(ISHR, "ISHR");
    map.put(LSHR, "LSHR");
    map.put(IUSHR, "IUSHR");
    map.put(LUSHR, "LUSHR");
    map.put(IAND, "IAND");
    map.put(LAND, "LAND");
    map.put(IOR, "IOR");
    map.put(LOR, "LOR");
    map.put(IXOR, "IXOR");
    map.put(LXOR, "LXOR");
    map.put(IINC, "IINC");
    map.put(I2L, "I2L");
    map.put(I2F, "I2F");
    map.put(I2D, "I2D");
    map.put(L2I, "L2I");
    map.put(L2F, "L2F");
    map.put(L2D, "L2D");
    map.put(F2I, "F2I");
    map.put(F2L, "F2L");
    map.put(F2D, "F2D");
    map.put(D2I, "D2I");
    map.put(D2L, "D2L");
    map.put(D2F, "D2F");
    map.put(I2B, "I2B");
    map.put(I2C, "I2C");
    map.put(I2S, "I2S");
    map.put(LCMP, "LCMP");
    map.put(FCMPL, "FCMPL");
    map.put(FCMPG, "FCMPG");
    map.put(DCMPL, "DCMPL");
    map.put(DCMPG, "DCMPG");
    map.put(IFEQ, "IFEQ");
    map.put(IFNE, "IFNE");
    map.put(IFLT, "IFLT");
    map.put(IFGE, "IFGE");
    map.put(IFGT, "IFGT");
    map.put(IFLE, "IFLE");
    map.put(IF_ICMPEQ, "IF_ICMPEQ");
    map.put(IF_ICMPNE, "IF_ICMPNE");
    map.put(IF_ICMPLT, "IF_ICMPLT");
    map.put(IF_ICMPGE, "IF_ICMPGE");
    map.put(IF_ICMPGT, "IF_ICMPGT");
    map.put(IF_ICMPLE, "IF_ICMPLE");
    map.put(IF_ACMPEQ, "IF_ACMPEQ");
    map.put(IF_ACMPNE, "IF_ACMPNE");
    map.put(GOTO, "GOTO");
    map.put(JSR, "JSR");
    map.put(RET, "RET");
    map.put(TABLESWITCH, "TABLESWITCH");
    map.put(LOOKUPSWITCH, "LOOKUPSWITCH");
    map.put(IRETURN, "IRETURN");
    map.put(LRETURN, "LRETURN");
    map.put(FRETURN, "FRETURN");
    map.put(DRETURN, "DRETURN");
    map.put(ARETURN, "ARETURN");
    map.put(RETURN, "RETURN");
    map.put(GETSTATIC, "GETSTATIC");
    map.put(PUTSTATIC, "PUTSTATIC");
    map.put(GETFIELD, "GETFIELD");
    map.put(PUTFIELD, "PUTFIELD");
    map.put(INVOKEVIRTUAL, "INVOKEVIRTUAL");
    map.put(INVOKESPECIAL, "INVOKESPECIAL");
    map.put(INVOKESTATIC, "INVOKESTATIC");
    map.put(INVOKEINTERFACE, "INVOKEINTERFACE");
    map.put(INVOKEDYNAMIC, "INVOKEDYNAMIC");
    map.put(NEW, "NEW");
    map.put(NEWARRAY, "NEWARRAY");
    map.put(ANEWARRAY, "ANEWARRAY");
    map.put(ARRAYLENGTH, "ARRAYLENGTH");
    map.put(ATHROW, "ATHROW");
    map.put(CHECKCAST, "CHECKCAST");
    map.put(INSTANCEOF, "INSTANCEOF");
    map.put(MONITORENTER, "MONITORENTER");
    map.put(MONITOREXIT, "MONITOREXIT");
    map.put(MULTIANEWARRAY, "MULTIANEWARRAY");
    map.put(IFNULL, "IFNULL");
    map.put(IFNONNULL, "IFNONNULL");
    return map;
  }

  private static Map<String, Integer> getReCodes() {
    HashMap<String, Integer> map = new HashMap<>();
    map.put("INVALID OPCODE", -1);
    map.put("ACC_PUBLIC", ACC_PUBLIC);
    map.put("ACC_PRIVATE", ACC_PRIVATE);
    map.put("ACC_PROTECTED", ACC_PROTECTED);
    map.put("ACC_STATIC", ACC_STATIC);
    map.put("ACC_FINAL", ACC_FINAL);
    map.put("ACC_SUPER", ACC_SUPER);
    map.put("ACC_SYNCHRONIZED", ACC_SYNCHRONIZED);
    map.put("ACC_VOLATILE", ACC_VOLATILE);
    map.put("ACC_BRIDGE", ACC_BRIDGE);
    map.put("ACC_VARARGS", ACC_VARARGS);
    map.put("ACC_TRANSIENT", ACC_TRANSIENT);
    map.put("ACC_NATIVE", ACC_NATIVE);
    map.put("ACC_INTERFACE", ACC_INTERFACE);
    map.put("ACC_ABSTRACT", ACC_ABSTRACT);
    map.put("ACC_STRICT", ACC_STRICT);
    map.put("ACC_SYNTHETIC", ACC_SYNTHETIC);
    map.put("ACC_ANNOTATION", ACC_ANNOTATION);
    map.put("ACC_ENUM", ACC_ENUM);
    map.put("ACC_MANDATED", ACC_MANDATED);
    map.put("ACC_DEPRECATED", ACC_DEPRECATED);
    map.put("T_BOOLEAN", T_BOOLEAN);
    map.put("T_CHAR", T_CHAR);
    map.put("T_FLOAT", T_FLOAT);
    map.put("T_DOUBLE", T_DOUBLE);
    map.put("T_BYTE", T_BYTE);
    map.put("T_SHORT", T_SHORT);
    map.put("T_INT", T_INT);
    map.put("T_LONG", T_LONG);
    map.put("H_GETFIELD", H_GETFIELD);
    map.put("H_GETSTATIC", H_GETSTATIC);
    map.put("H_PUTFIELD", H_PUTFIELD);
    map.put("H_PUTSTATIC", H_PUTSTATIC);
    map.put("H_INVOKEVIRTUAL", H_INVOKEVIRTUAL);
    map.put("H_INVOKESTATIC", H_INVOKESTATIC);
    map.put("H_INVOKESPECIAL", H_INVOKESPECIAL);
    map.put("H_NEWINVOKESPECIAL", H_NEWINVOKESPECIAL);
    map.put("H_INVOKEINTERFACE", H_INVOKEINTERFACE);
    map.put("F_NEW", F_NEW);
    map.put("F_FULL", F_FULL);
    map.put("F_APPEND", F_APPEND);
    map.put("F_CHOP", F_CHOP);
    map.put("F_SAME", F_SAME);
    map.put("F_SAME1", F_SAME1);
    map.put("NOP", NOP);
    map.put("ACONST_NULL", ACONST_NULL);
    map.put("ICONST_M1", ICONST_M1);
    map.put("ICONST_0", ICONST_0);
    map.put("ICONST_1", ICONST_1);
    map.put("ICONST_2", ICONST_2);
    map.put("ICONST_3", ICONST_3);
    map.put("ICONST_4", ICONST_4);
    map.put("ICONST_5", ICONST_5);
    map.put("LCONST_0", LCONST_0);
    map.put("LCONST_1", LCONST_1);
    map.put("FCONST_0", FCONST_0);
    map.put("FCONST_1", FCONST_1);
    map.put("FCONST_2", FCONST_2);
    map.put("DCONST_0", DCONST_0);
    map.put("DCONST_1", DCONST_1);
    map.put("BIPUSH", BIPUSH);
    map.put("SIPUSH", SIPUSH);
    map.put("LDC", LDC);
    map.put("ILOAD", ILOAD);
    map.put("LLOAD", LLOAD);
    map.put("FLOAD", FLOAD);
    map.put("DLOAD", DLOAD);
    map.put("ALOAD", ALOAD);
    map.put("IALOAD", IALOAD);
    map.put("LALOAD", LALOAD);
    map.put("FALOAD", FALOAD);
    map.put("DALOAD", DALOAD);
    map.put("AALOAD", AALOAD);
    map.put("BALOAD", BALOAD);
    map.put("CALOAD", CALOAD);
    map.put("SALOAD", SALOAD);
    map.put("ISTORE", ISTORE);
    map.put("LSTORE", LSTORE);
    map.put("FSTORE", FSTORE);
    map.put("DSTORE", DSTORE);
    map.put("ASTORE", ASTORE);
    map.put("IASTORE", IASTORE);
    map.put("LASTORE", LASTORE);
    map.put("FASTORE", FASTORE);
    map.put("DASTORE", DASTORE);
    map.put("AASTORE", AASTORE);
    map.put("BASTORE", BASTORE);
    map.put("CASTORE", CASTORE);
    map.put("SASTORE", SASTORE);
    map.put("POP", POP);
    map.put("POP2", POP2);
    map.put("DUP", DUP);
    map.put("DUP_X1", DUP_X1);
    map.put("DUP_X2", DUP_X2);
    map.put("DUP2", DUP2);
    map.put("DUP2_X1", DUP2_X1);
    map.put("DUP2_X2", DUP2_X2);
    map.put("SWAP", SWAP);
    map.put("IADD", IADD);
    map.put("LADD", LADD);
    map.put("FADD", FADD);
    map.put("DADD", DADD);
    map.put("ISUB", ISUB);
    map.put("LSUB", LSUB);
    map.put("FSUB", FSUB);
    map.put("DSUB", DSUB);
    map.put("IMUL", IMUL);
    map.put("LMUL", LMUL);
    map.put("FMUL", FMUL);
    map.put("DMUL", DMUL);
    map.put("IDIV", IDIV);
    map.put("LDIV", LDIV);
    map.put("FDIV", FDIV);
    map.put("DDIV", DDIV);
    map.put("IREM", IREM);
    map.put("LREM", LREM);
    map.put("FREM", FREM);
    map.put("DREM", DREM);
    map.put("INEG", INEG);
    map.put("LNEG", LNEG);
    map.put("FNEG", FNEG);
    map.put("DNEG", DNEG);
    map.put("ISHL", ISHL);
    map.put("LSHL", LSHL);
    map.put("ISHR", ISHR);
    map.put("LSHR", LSHR);
    map.put("IUSHR", IUSHR);
    map.put("LUSHR", LUSHR);
    map.put("IAND", IAND);
    map.put("LAND", LAND);
    map.put("IOR", IOR);
    map.put("LOR", LOR);
    map.put("IXOR", IXOR);
    map.put("LXOR", LXOR);
    map.put("IINC", IINC);
    map.put("I2L", I2L);
    map.put("I2F", I2F);
    map.put("I2D", I2D);
    map.put("L2I", L2I);
    map.put("L2F", L2F);
    map.put("L2D", L2D);
    map.put("F2I", F2I);
    map.put("F2L", F2L);
    map.put("F2D", F2D);
    map.put("D2I", D2I);
    map.put("D2L", D2L);
    map.put("D2F", D2F);
    map.put("I2B", I2B);
    map.put("I2C", I2C);
    map.put("I2S", I2S);
    map.put("LCMP", LCMP);
    map.put("FCMPL", FCMPL);
    map.put("FCMPG", FCMPG);
    map.put("DCMPL", DCMPL);
    map.put("DCMPG", DCMPG);
    map.put("IFEQ", IFEQ);
    map.put("IFNE", IFNE);
    map.put("IFLT", IFLT);
    map.put("IFGE", IFGE);
    map.put("IFGT", IFGT);
    map.put("IFLE", IFLE);
    map.put("IF_ICMPEQ", IF_ICMPEQ);
    map.put("IF_ICMPNE", IF_ICMPNE);
    map.put("IF_ICMPLT", IF_ICMPLT);
    map.put("IF_ICMPGE", IF_ICMPGE);
    map.put("IF_ICMPGT", IF_ICMPGT);
    map.put("IF_ICMPLE", IF_ICMPLE);
    map.put("IF_ACMPEQ", IF_ACMPEQ);
    map.put("IF_ACMPNE", IF_ACMPNE);
    map.put("GOTO", GOTO);
    map.put("JSR", JSR);
    map.put("RET", RET);
    map.put("TABLESWITCH", TABLESWITCH);
    map.put("LOOKUPSWITCH", LOOKUPSWITCH);
    map.put("IRETURN", IRETURN);
    map.put("LRETURN", LRETURN);
    map.put("FRETURN", FRETURN);
    map.put("DRETURN", DRETURN);
    map.put("ARETURN", ARETURN);
    map.put("RETURN", RETURN);
    map.put("GETSTATIC", GETSTATIC);
    map.put("PUTSTATIC", PUTSTATIC);
    map.put("GETFIELD", GETFIELD);
    map.put("PUTFIELD", PUTFIELD);
    map.put("INVOKEVIRTUAL", INVOKEVIRTUAL);
    map.put("INVOKESPECIAL", INVOKESPECIAL);
    map.put("INVOKESTATIC", INVOKESTATIC);
    map.put("INVOKEINTERFACE", INVOKEINTERFACE);
    map.put("INVOKEDYNAMIC", INVOKEDYNAMIC);
    map.put("NEW", NEW);
    map.put("NEWARRAY", NEWARRAY);
    map.put("ANEWARRAY", ANEWARRAY);
    map.put("ARRAYLENGTH", ARRAYLENGTH);
    map.put("ATHROW", ATHROW);
    map.put("CHECKCAST", CHECKCAST);
    map.put("INSTANCEOF", INSTANCEOF);
    map.put("MONITORENTER", MONITORENTER);
    map.put("MONITOREXIT", MONITOREXIT);
    map.put("MULTIANEWARRAY", MULTIANEWARRAY);
    map.put("IFNULL", IFNULL);
    map.put("IFNONNULL", IFNONNULL);
    return map;
  }

  /**
   * Given an opcode (as text), returns the opcode as an
   * index.
   *
   * @param opcode
   * @return
   */
  public static int getOpcodeIndex(String opcode) {
    return reCodes.get(opcode.toUpperCase());
  }

  /**
   * Given an opcode (index), returns the opcode as text.
   *
   * @param opcode
   * @return
   */
  public static String getOpcodeText(int opcode) {
    return opCodes.get(opcode);
  }

  /**
   * Returns a set of all the opcodes as strings.
   *
   * @return
   */
  public static Set<String> getOpcodes() {
    return reCodes.keySet();
  }

  /**
   * Get the integer value of a InsnNode.
   *
   * @param ain
   * @return
   */
  public static int getIntValue(AbstractInsnNode ain) {
    int opcode = ain.getOpcode();
    if (opcode >= ICONST_M1 && opcode <= ICONST_5) {
      return getIntValue(opcode);
    } else {
      return ((IntInsnNode) ain).operand;
    }
  }

  /**
   * Gets the integer value of a given opcode.
   *
   * @param opcode
   * @return
   */
  public static int getIntValue(int opcode) {
    if (opcode == ICONST_0) {
      return 0;
    } else if (opcode == ICONST_1) {
      return 1;
    } else if (opcode == ICONST_2) {
      return 2;
    } else if (opcode == ICONST_3) {
      return 3;
    } else if (opcode == ICONST_4) {
      return 4;
    } else if (opcode == ICONST_5) {
      return 5;
    } else {
      return -1;
    }
  }

  public static String getHandleOpcodeText(int hop) {
    switch (hop) {
      case H_GETFIELD:
        return "H_GETFIELD";
      case H_GETSTATIC:
        return "H_GETSTATIC";
      case H_PUTFIELD:
        return "H_PUTFIELD";
      case H_PUTSTATIC:
        return "H_PUTSTATIC";
      case H_INVOKEVIRTUAL:
        return "H_INVOKEVIRTUAL";
      case H_INVOKESTATIC:
        return "H_INVOKESTATIC";
      case H_INVOKESPECIAL:
        return "H_INVOKESPECIAL";
      case H_NEWINVOKESPECIAL:
        return "H_NEWINVOKESPECIAL";
      case H_INVOKEINTERFACE:
        return "H_INVOKEINTERFACE";
    }
    return null;
  }

  /**
   * Gets the index of a AbstractInsnNode.
   *
   * @param ain
   * @return
   */
  public static int getIndex(AbstractInsnNode ain) {
    int index = 0;
    while (ain.getPrevious() != null) {
      ain = ain.getPrevious();
      index += 1;
    }
    return index;
  }

  public static String getFrameType(int type) {
    switch (type) {
      case F_NEW:
        return "F_NEW";
      case F_FULL:
        return "F_FULL";
      case F_APPEND:
        return "F_APPEND";
      case F_CHOP:
        return "F_CHOP";
      case F_SAME:
        return "F_SAME";
      case F_SAME1:
        return "F_SAME1";
    }
    return "FRAME";
  }

  /**
   * Given an integer, returns an InsnNode that will
   * properly represent the int.
   *
   * @param i
   * @return
   */
  public static AbstractInsnNode toInt(int i) {
    switch (i) {
      case -1:
        return new InsnNode(ICONST_M1);
      case 0:
        return new InsnNode(ICONST_0);
      case 1:
        return new InsnNode(ICONST_1);
      case 2:
        return new InsnNode(ICONST_2);
      case 3:
        return new InsnNode(ICONST_3);
      case 4:
        return new InsnNode(ICONST_4);
      case 5:
        return new InsnNode(ICONST_5);
    }
    if (i > -129 && i < 128) {
      return new IntInsnNode(BIPUSH, i);
    }
    return new LdcInsnNode(i);
  }

  public static int getLabelIndex(AbstractInsnNode ain) {
    int index = 0;
    AbstractInsnNode node = ain;
    while (node.getPrevious() != null) {
      node = node.getPrevious();
      if (node.getType() == AbstractInsnNode.LABEL) {
        index += 1;
      }
    }
    return index;
  }
}