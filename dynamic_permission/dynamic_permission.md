# Android 运行时请求权限


> 从 Android 6.0（API 级别 23）开始，用户开始在应用运行时向其授予权限，而不是在应用安装时授予。如果应用在清单中列出正常权限，系统会自动授予，如果应用列出的是[危险权限](https://developer.android.com/guide/topics/security/permissions.html#normal-dangerous)时，则必须得到用户的批准。 
 
[官方介绍](https://developer.android.com/training/permissions/requesting.html)

## 什么是权限
首先假设有个功能，点击按钮拨打电话。

```java
findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {
  		Intent intent = new Intent(Intent.ACTION_CALL);
  		Uri data = Uri.parse("tel:" + "000000000");
  		intent.setData(data);
  		startActivity(intent);
  		}
  	});
```  
拨打电话需要 `android.permission.CALL_PHONE` 这个权限，我们需要将其配置到清单文件中。

```xml
<uses-permission android:name="android.permission.CALL_PHONE"/>
```  

##### 没有配置  
运行时会崩溃，报`java.lang.SecurityException: Permission Denial`  异常，这一点在任何版本的android系统上都是一样的  
  
##### 配置完成  
* 6.0 以下版本的Android系统上，可以成功拨打电话
* 6.0 及以上版本的Android系统上，依旧崩溃，还是报了`java.lang.SecurityException: Permission Denial`的异常

对于6.0以上的手机，我们进入手机的应用管理，找到我们的应用，然后进入权限控制，发现拨打电话权限并没有处于开启的状态。   
![](image/N001.png)  
然后我们手动开启权限，再回到应用，这时，果然可以正常拨打电话了。于是乎，我们告诉用户，安装完成我们的应用之后，请你通过设置进入应用管理，然后找到我们的应用，进入权限管理，开启拨打电话的权限。听完后用户默默的选择了卸载。  

## 检查和请求权限
Android 框架从 Android 6.0（API 级别 23）开始提供了**检查和请求权限**的方法,不过我们通常使用support库来完成,因为使用支持库更简单，因为在调用方法前，应用不需要检查它在哪个版本的 Android 上运行。   

### 检查权限
我们需要`ContextCompat`类的 [`checkSelfPermission`](https://developer.android.com/reference/android/support/v4/content/ContextCompat.html#checkSelfPermission\(android.content.Context\, java.lang.String\)) 方法来检查当前手机是否拥有某个权限
  
我们来调整我们的代码  

``` java
    @Override
    public void onClick(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            //没有 CALL_PHONE 权限
        }else{
            //拥有 CALL_PHONE 权限
            callPhone();
        }
    }

    public void callPhone(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "18813149271");
        intent.setData(data);
        startActivity(intent);
    }
```
从代码中可以看出来，有打电话权限的时候我们才去调用拨打电话的方法。  
运行一下，程序果然没有崩，但也没有拨打电话，因为没有权限的时候我们什么也没做，接下来我们就要在没有权限的时候请求我们需要的权限了。  

### 请求权限

我们需要`ActivityCompat`类的[`requestPermissions`](https://developer.android.com/reference/android/support/v4/app/ActivityCompat.html#requestPermissions(android.app.Activity, java.lang.String[], int))方法来申请我么需要的权限，这个方法每次申请的是权限字符串的数组，也就是每次可以申请多个权限。申请完之后，我们需要处理申请的结果，是同意还是拒绝。  
  
当用户响应我们申请的权限时，系统将调用应用的 [`onRequestPermissionsResult`](https://developer.android.com/reference/android/support/v4/app/ActivityCompat.OnRequestPermissionsResultCallback.html#onRequestPermissionsResult(int, java.lang.String[], int[]))，我们重写这个方法来处理申请的结果(类似 activity 跳转中的 `onActivityResult`)

再来调整一下代码

```java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                    callPhone();
                } else {
                	//申请拒绝
                	Toast.makeText(this, "您已拒绝xxx的权限，...", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

```  

再次运行，点击拨打电话，弹出了申请权限的对话框，我们有同意和拒绝两个选择

![](image/N002.png)

#### 同意  
拨打电话，进入应用的权限管理页面，发现应用已经有次权限，下次用到该权限时，在检查权限阶段就会自动通过检测。

#### 拒绝  
**普通拒绝 ：** 会执行我们在上面代码中“申请拒绝”处的部分，下次在申请时，申请权限的对话框会多出现一个‘不在询问’的勾选框。

![](image/N003.png)  

**不在询问 + 拒绝 ：** 下次申请权限，不会弹出，直接拒绝，相当于系统帮你点击了拒绝，再想获得该权限，就只有手动到应用权限管理界面处理

### 提示用户  

有时候我们需要向用户解释为什么我们需要改该权限，Android官方建议我们不要提供过多的解释，只需在用户之前拒绝过该项权限，然后再次申请的时候提供解释。于是官方给我们提供了这么一个方法 [`shouldShowRequestPermissionRationale()`](https://developer.android.com/reference/android/support/v4/app/ActivityCompat.html#shouldShowRequestPermissionRationale(android.app.Activity, java.lang.String))。

下面的代码，弹了一个对话来模拟一下提示

```java
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //没有 CALL_PHONE 权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
                new AlertDialog.Builder(this)
                        .setTitle("申请权限")
                        .setMessage("我需要拥有xxx权限，去完成...")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},CALL_PHONE_REQUEST_CODE);
                            }
                        })
                        .show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},CALL_PHONE_REQUEST_CODE);
            }

        } else {
            //拥有 CALL_PHONE 权限
            callPhone();
        }
```

#### 关于`shouldShowRequestPermissionRationale()`的返回值
**True**   

* 如果用户之前拒绝了权限的申请就会返回true(如果在拒绝的同时勾选了‘不在询问’，则返回的是false)
* 如果同意过，然后手动在应用的权限管理中关闭了该权限，这就等价于在申请权限的时候选择了拒绝，同样是返回true

**False**  
其余情况返回的都是false





 







