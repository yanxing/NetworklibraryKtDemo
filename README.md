# NetworklibraryKtDemo
功能和[networklibrary](https://github.com/yanxing/NetworklibraryDemo) 一样。

请求示例
```kotlin
RetrofitManage.request(this, { serviceAPI.getWeather("上海") }, {
            //success挂起函数，业务层面成功，必写
            content.text = it.toString()
        }
        /*, {
            //error挂起函数，业务层面报错，可以不写此部分
        }, {
            //catch挂起函数，请求报错，以不写此部分
        }, {
            //complete挂起函数，请求完成，以不写此部分
        }*/
        )
```
