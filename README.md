# NetworklibraryKtDemo

请求示例
```kotlin
RetrofitManage.request(this,{ serviceAPI.getWeather("上海") },
    object : SimpleAbstractObserver<Weather>() {
        override fun onCall(value: Weather) {
                    
        }
    })
```

未完待续...
