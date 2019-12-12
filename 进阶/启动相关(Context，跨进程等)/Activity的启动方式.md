# Activity的启动方式

## 在Service中启动Activity

* 在Service中启动Activity必须setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)，这是因为service.startActivity()->ContextWrapper.startActivity()->ContextImpl.startActivity()。再ContextImpl.startActivity里面会检查Intent的参数是否包含FLAG_ACTIVITY_NEW_TASK
* 如果加载某个Activity的intent，Falg设置成FLAG_ACTIVITY_NEW_TASK时，它会首先检查是否存在与自己taskAffinity相同的Task，如果存在，那么它会直接到该Task中，如果不存在则重新创建Task

