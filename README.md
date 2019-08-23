此为使用注解生成代码，处理运行时权限
使用方式为加入annotation注解包，compiler注解处理器的包，permissionLib权限管理的包
implementation project(':annotations')
implementation project(':permissionlib')
annotationProcessor project(':compiler')

一共有4个annotation：
NeedsPermission获取权限之后执行操作，OnPermissionDenied当权限被拒绝之后执行的操作，OnNeverAskAgain当点击不再询问之后执行的操作，
OnShowRationale当此权限并没有执行不再提示时进行请求的提示与操作

一个概念：shouldShowRequestPermissionRationale这个方法检查被拒绝的权限组中，是否有点击了“不再询问”权限
第一次打开App是返回false
上次弹出权限点击了拒绝，并未点击不再询问  true
上次弹出权限点击了拒绝，并点击了不再询问  false
点击拒绝，未勾选不再询问 true
点击拒绝，勾选了不再询问 false

核心为compiler注解处理器，本次是直接写的字符串，下次用javapoet能更简洁一些
