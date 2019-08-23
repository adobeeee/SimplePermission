package cn.ld.compiler;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import cn.ld.annotations.NeedsPermission;
import cn.ld.annotations.OnNeverAskAgain;
import cn.ld.annotations.OnPermissionDenied;
import cn.ld.annotations.OnShowRationale;

/**
 * Created by ld on 2019/8/22.
 * 将这个类注册到google的服务中，就像activity注册到manifest中一样
 */
@AutoService(Processor.class)
//@SupportedAnnotationTypes({Constant.NEEDS_PERMISSION_ANNOTATION_TYPE,Constant.ON_NEVER_ASK_AGAIN_ANNOTATION_TYPE})
public class PermissionProcessor extends AbstractProcessor {

    //用于日志的输出，警告错误等
    private Messager messager;
    //文件处理器
    private Filer filer;
    //操作元素的工具
    private Elements elementsUtils;
    private Types typeUtils;


    /**
     * @author ld
     * created at 2019/8/22:17:01
     * 初始化，执行一次，一般用来赋值
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager=processingEnvironment.getMessager();
        filer=processingEnvironment.getFiler();
        elementsUtils=processingEnvironment.getElementUtils();
        typeUtils=processingEnvironment.getTypeUtils();

    }


    /**
     * @author ld
     * created at 2019/8/22:17:02
     * 支持的注解类型，必须加入不然无法进入process
     * 需要处理哪些注解,添加了这些类型的注解，就会去扫描项目中用到了这些注解的地方，只要用到了，就会生成一个文件
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NeedsPermission.class.getCanonicalName());
        types.add(OnNeverAskAgain.class.getCanonicalName());
        types.add(OnPermissionDenied.class.getCanonicalName());
        types.add(OnShowRationale.class.getCanonicalName());
        return types;
    }
    /**
     * @author ld
     * created at 2019/8/22:17:03
     * 当前编译的环境，jdk的多少版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        //返回此注释Processor支持的最新的原版本，该方法可以通过注释@SupportedSourceVersion(SourceVersion.RELEASE_7)定义
        return SourceVersion.RELEASE_7;
    }

    /**
     * @author ld
     * Create at 2019/8/23 20:47
     * 接收参数，可以再gradle中配置
     */
    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    /**
     * @author ld
     * created at 2019/8/22:17:03
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) return false;

        //获取某个activity中所有带NeedsPermissions注解的方法
        Set<? extends Element> needsPermissionSet= roundEnvironment.getElementsAnnotatedWith(NeedsPermission.class);
        //key是activity,value是这个activity中所有带此注解的方法的集合
        Map<String, List<ExecutableElement>> needsPermissionMap=new HashMap<>();
        //遍历所有带NeedsPermission的方法
        for (Element element : needsPermissionSet) {
            //转成原始属性元素，结构元素
            ExecutableElement executableElement= (ExecutableElement) element;
            //通过属性元素获取它所属的类名
            String activityName=getActivityName(executableElement);
            //从缓存结合中获取所有带注解的方法的集合
            List<ExecutableElement> list=needsPermissionMap.get(activityName);
            if (list==null){
                list=new ArrayList<>();
                needsPermissionMap.put(activityName,list);
            }
            list.add(executableElement);
        }


        //获取各个activity中所有带OnNeverAskAgain注解的方法
        Set<? extends Element> onNeverPermissionSet= roundEnvironment.getElementsAnnotatedWith(OnNeverAskAgain.class);
        Map<String, List<ExecutableElement>> onNeverPermissionMap=new HashMap<>();
        for (Element element : onNeverPermissionSet) {
            ExecutableElement executableElement= (ExecutableElement) element;
            String activityName=getActivityName(executableElement);
            List<ExecutableElement> list=onNeverPermissionMap.get(activityName);
            if (list==null){
                list=new ArrayList<>();
                onNeverPermissionMap.put(activityName,list);
            }
            list.add(executableElement);
        }

        //获取某个activity中所有带OnPermissionDenied注解的方法
        Set<? extends Element> OnPermissionDeniedSet= roundEnvironment.getElementsAnnotatedWith(OnPermissionDenied.class);
        Map<String, List<ExecutableElement>> OnPermissionDeniedMap=new HashMap<>();
        for (Element element : OnPermissionDeniedSet) {
            ExecutableElement executableElement= (ExecutableElement) element;
            String activityName=getActivityName(executableElement);
            List<ExecutableElement> list=OnPermissionDeniedMap.get(activityName);
            if (list==null){
                list=new ArrayList<>();
                OnPermissionDeniedMap.put(activityName,list);
            }
            list.add(executableElement);
        }

        //获取某个activity中所有带OnShowRationale注解的方法
        Set<? extends Element> OnShowRationaleSet= roundEnvironment.getElementsAnnotatedWith(OnShowRationale.class);
        Map<String, List<ExecutableElement>> OnShowRationaleMap=new HashMap<>();
        for (Element element : OnShowRationaleSet) {
            ExecutableElement executableElement= (ExecutableElement) element;
            String activityName=getActivityName(executableElement);
            List<ExecutableElement> list=OnShowRationaleMap.get(activityName);
            if (list==null){
                list=new ArrayList<>();
                OnShowRationaleMap.put(activityName,list);
            }
            list.add(executableElement);
        }

        //获取activity完整的字符串类名（包名+类名）
        //遍历所有携带注解方法的activity，并生成代码
        for (String activityName:needsPermissionMap.keySet()){
            //获取某个activity中所有控件方法的集合
            List<ExecutableElement> needsPermissionElements=needsPermissionMap.get(activityName);
            List<ExecutableElement> onNeverAskAgainElements=onNeverPermissionMap.get(activityName);
            List<ExecutableElement> onPermissionDeniedElements=OnPermissionDeniedMap.get(activityName);
            List<ExecutableElement> onShowRationaleElements=OnShowRationaleMap.get(activityName);

            final String CLASS_SUFFIX="$$Permissions";
            Filer filer=processingEnv.getFiler();

            try {
                //创建一个新的文件，并返回一个对象允许它被写入
                JavaFileObject javaFileObject=filer.createSourceFile(activityName+CLASS_SUFFIX);
                //通过方法标签获取包名标签（任意一个属性标签的父节点都是同一个包名）
                String packageName=getPackageName(needsPermissionElements.get(0));
                //定义Writer对象，开启造币过程
                Writer writer = javaFileObject.openWriter();

                //类名：此时的类名不包含包名
                //通过属性元素获取它所属的activity类名，再拼接后结果activityName$$Permissions
                String activitySimpleName=needsPermissionElements.get(0).getEnclosingElement().getSimpleName().toString()+CLASS_SUFFIX;

                //开始造币----------------------------
                writer.write("package "+packageName+";\n");
                //生成要导入的接口类，需要手动导入
                writer.write("import cn.ld.permissionlib.listener.RequestPermission;\n");
                writer.write("import cn.ld.permissionlib.listener.PermissionRequest;\n");
                writer.write("import cn.ld.permissionlib.listener.PermissionSetting;\n");
                writer.write("import cn.ld.permissionlib.util.PermissionUtils;\n");
                writer.write("import android.support.v7.app.AppCompatActivity;\n");
                writer.write("import android.support.v4.app.ActivityCompat;\n");
                writer.write("import android.content.Intent;\n");
                writer.write("import android.net.Uri;\n");
                writer.write("import android.provider.Settings;\n");
                writer.write("import android.util.Log;\n");
                writer.write("import android.support.annotation.NonNull;\n");
                writer.write("import java.lang.ref.WeakReference;\n");

                //生成类
                writer.write("public class "+ activitySimpleName+" implements RequestPermission<"+activityName+">{\n");
                //生成属性
                writer.write("private static final int REQUEST_PERMISSION_CODE = 666;\n");
                writer.write("private static String[]  NEEDS_PERMISSION;\n");

                //生成requestPermission方法
                writer.write("public void requestPermission(" + activityName+" target , String[] permissions){\n");
                writer.write("NEEDS_PERMISSION = permissions;\n");
                writer.write("if (PermissionUtils.hasSelfPermissions(target, NEEDS_PERMISSION)){\n");
                //循环生成Activity每个权限的方法
                for (ExecutableElement executableElement:needsPermissionElements){
                    String methodName = executableElement.getSimpleName().toString();
                    writer.write("target."+methodName+"();\n");
                }
                writer.write("} else if(PermissionUtils.shouldShowRequestPermissionRationale(target,NEEDS_PERMISSION)){\n");
                //循环生成activity每个提示用户为何要开启权限的方法
                if (onShowRationaleElements !=null&&!onShowRationaleElements.isEmpty()){
                    for (ExecutableElement executableElement :onShowRationaleElements){
                        //获取方法名
                        String methodName=executableElement.getSimpleName().toString();
                        // 调用提示用户为何要开启权限方法
                        writer.write("target." + methodName + "(new PermissionRequestImpl(target));\n");
                    }
                }
                writer.write("} else {\n");
                writer.write("ActivityCompat.requestPermissions(target, NEEDS_PERMISSION, REQUEST_PERMISSION_CODE);\n}\n}\n");

                // 生成onRequestPermissionsResult方法
                writer.write("public void onRequestPermissionResult(" + activityName + " target, int requestCode, @NonNull int[] grantResults) {");
                writer.write("switch(requestCode) {\n");
                writer.write("case REQUEST_PERMISSION_CODE:\n");
                writer.write("if (PermissionUtils.verifyPermissions(grantResults)) {\n");

                // 循环生成MainActivity每个权限申请方法
                for (ExecutableElement executableElement : needsPermissionElements) {
                    // 获取方法名
                    String methodName = executableElement.getSimpleName().toString();
                    // 调用申请权限方法
                    writer.write("target." + methodName + "();\n");
                }

                writer.write("} else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, NEEDS_PERMISSION)) {\n");

                // 循环生成MainActivity每个不再询问后的提示
                for (ExecutableElement executableElement : onNeverAskAgainElements) {
                    // 获取方法名
                    String methodName = executableElement.getSimpleName().toString();
                    // 调用不再询问后的提示
                    writer.write("target." + methodName + "(new PermissionSettingImpl(target));\n");
                }

                writer.write("} else {\n");

                // 循环生成MainActivity每个拒绝时的提示方法
                for (ExecutableElement executableElement : onPermissionDeniedElements) {
                    // 获取方法名
                    String methodName = executableElement.getSimpleName().toString();
                    // 调用拒绝时的提示方法
                    writer.write("target." + methodName + "();\n");
                }

                writer.write("}\nbreak;\ndefault:\nbreak;\n}\n}\n");

                // 生成接口实现类：PermissionRequestImpl implements PermissionRequest
                writer.write("private static final class PermissionRequestImpl implements PermissionRequest {\n");
                writer.write("private final WeakReference<" + activityName + "> weakTarget;\n");
                writer.write("private PermissionRequestImpl(" + activityName + " target) {\n");
                writer.write("this.weakTarget = new WeakReference(target);\n}\n");
                writer.write("public void proceed() {\n");
                writer.write(activityName + " target = (" + activityName + ")this.weakTarget.get();\n");
                writer.write("if (target != null) {\n");
                writer.write("ActivityCompat.requestPermissions(target, NEEDS_PERMISSION, REQUEST_PERMISSION_CODE);\n}\n}\n}\n");

                // 生成接口实现类：PermissionSettingImpl implements PermissionSetting
                writer.write("private static final class PermissionSettingImpl implements PermissionSetting {\n");
                writer.write("private final WeakReference<" + activityName + "> weakTarget;\n");
                writer.write("private PermissionSettingImpl(" + activityName + " target) {\n");
                writer.write("this.weakTarget = new WeakReference(target);\n}\n");
                writer.write("public void setting(int settingCode) {\n");
                writer.write(activityName + " target = (" + activityName + ")this.weakTarget.get();\n");
                writer.write("if (target != null) {\n");
                writer.write("Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);\n");
                writer.write("Uri uri = Uri.fromParts(\"package\", target.getPackageName(), null);\n");
                writer.write("intent.setData(uri);\n");
                writer.write("target.startActivityForResult(intent, settingCode);\n}\n}\n}\n");

                // 最后结束标签，造币完成
                writer.write("\n}");
                System.out.println("结束 ----------------------------------->");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return false;
    }


    private String getActivityName(ExecutableElement executableElement){
        String packageName=getPackageName(executableElement);
        TypeElement typeElement= (TypeElement) executableElement.getEnclosingElement();
        return packageName+"."+typeElement.getSimpleName().toString();
    }

    private String getPackageName(ExecutableElement executableElement) {
        //通过方法标签获取类名标签
        TypeElement typeElement= (TypeElement) executableElement.getEnclosingElement();
        //通过类名标签获取包名标签
        String packageName=processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        return packageName;


    }
}
