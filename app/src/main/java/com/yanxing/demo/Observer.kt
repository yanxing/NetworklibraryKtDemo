package com.yanxing.demo

import com.yanxing.networklibrarykt.model.ResultModel
import kotlinx.coroutines.flow.FlowCollector

/**
 * @author 李双祥 on 2020/7/30.
 */


fun <E> AbstractObserver<E>.suspend(action:  () -> Unit): AbstractObserver<E> {
    return this
}

fun <E> AbstractObserver<E>.onCall(action: suspend (value: ResultModel<E>) -> Unit): AbstractObserver<E> {
    //value?.let { action(it) }
    return object :AbstractObserver<E>{
        override suspend fun col(c: ResultModel<E>) {
            action.invoke(c)
        }

    }
    //return this
}

private suspend fun <T> AbstractObserver<T>.invokeSafely(
    action: suspend AbstractObserver<T>.(value: ResultModel<T>) -> Unit,
    value: ResultModel<T>
) {
    action(value)
}
//
//fun <E> AbstractObserver<E>.onError(action:  (cause: Throwable) -> Unit): AbstractObserver<E> {
//    cause?.let { action(it) }
//    return this
//}
//
//fun <E> AbstractObserver<E>.onComplete(action:suspend  (cause: Throwable?) -> Unit): AbstractObserver<E> {
//    cause?.let {
//        // un(action)
//    }
//    return this
//}

interface AbstractObserver<E>{

    public suspend fun col(c: ResultModel<E>)



}

inline  fun <E>  un(crossinline block:suspend ResultModel<E>.()->Unit): Observer<E> {
    return object :Observer<E>{
        override suspend fun col(c: ResultModel<E>) {
            c.block()
        }

    }
}

interface Observer<E>{

    public suspend fun col(c: ResultModel<E>)
}