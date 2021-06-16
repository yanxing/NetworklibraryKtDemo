# NetworklibraryKtDemo
功能和[networklibrary](https://github.com/yanxing/NetworklibraryDemo) 一样。
使用viewModelScope根据生命周期自动取消，不用修改基类处理rxjava可能导致的内存泄漏。
gradle接入，mavenCentral

```java
implementation 'io.github.yanxing:networklibrary-ktx:1.0.0'
```

请求示例
```kotlin
RetrofitManage.request(this, { serviceAPI.getWeather("上海") }, {
            //success挂起函数，业务层面状态码成功data数据，必写
            content.text = it.toString()
        }, {
            //error挂起函数，业务层面状态码失败，可以不写此部分
        }, {
            //catch挂起函数，请求报错，可以不写此部分
        }, {
            //complete挂起函数，请求完成，可以不写此部分
        }, {
            //collect挂起函数，ResultModel<T>数据，可以不写此部分
        })
```
