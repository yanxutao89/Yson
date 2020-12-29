package asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import test.BaseTypeVo;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.*;

/**
 * @Author: Yanxt7
 * @Desc:
 * @Date: 2020/12/29 10:59
 */
public class AddTimerAdapter extends ClassVisitor {

	private String owner;
	private boolean isInterface;

	public AddTimerAdapter() {
		super(ASM4);
	}

	public AddTimerAdapter(ClassVisitor classVisitor) {
		super(ASM4, classVisitor);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		this.owner = name;
		isInterface = (access & ACC_INTERFACE) != 0;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
		if (!isInterface && null != methodVisitor && !"<init>".equals(name) && name.startsWith("set")) {
			methodVisitor = new AddTimerMethodAdapter(methodVisitor);
		}
		return methodVisitor;
	}
}
class AddTimerMethodAdapter extends MethodVisitor {

	public AddTimerMethodAdapter() {
		super(ASM4);
	}

	public AddTimerMethodAdapter(MethodVisitor methodVisitor) {
		super(ASM4, methodVisitor);
	}

	@Override
	public void visitCode() {
		mv.visitCode();
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J");
		mv.visitVarInsn(LSTORE, 2);
	}

	@Override
	public void visitInsn(int opcode) {
		if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
			mv.visitMethodInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J");
			mv.visitVarInsn(LLOAD, 2);
			mv.visitInsn(LSUB);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "println", "(J)V");
		}
		mv.visitInsn(opcode);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		mv.visitMaxs(maxStack + 4, maxLocals);
	}

	public static void main(String[] args) throws Exception {
		String name = BaseTypeVo.class.getName();
		ClassReader classReader = new ClassReader(name);
		ClassWriter classWriter = new ClassWriter(0);
		CheckClassAdapter checkClassAdapter = new CheckClassAdapter(classWriter);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(checkClassAdapter, new ASMifier(), new PrintWriter(new FileOutputStream(name)));
		AddTimerAdapter addTimerAdapter = new AddTimerAdapter(traceClassVisitor);
		classReader.accept(addTimerAdapter, 0);
		byte[] bytes = classWriter.toByteArray();
		MyClassLoader myClassLoader = new MyClassLoader();
		Class defineClass = myClassLoader.defineClass(name, bytes);
		BaseTypeVo baseTypeVo = (BaseTypeVo) defineClass.newInstance();
		baseTypeVo.setString(name);

	}
}
