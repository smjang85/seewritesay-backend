package org.lena.support

import java.lang.reflect.Field

object BaseTestUtils {

    /**
     * 리플렉션을 이용해 Entity의 id 값을 설정합니다.
     * 일반적으로 테스트 전용으로 사용됩니다.
     */
    fun <T : Any> setId(entity: T, id: Long) {
        val idField: Field = entity.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }

    /**
     * 여러 Entity의 id 값을 순차적으로 설정할 때 사용
     */
    fun <T : Any> setIds(entities: List<T>, startingId: Long = 1L) {
        var currentId = startingId
        for (entity in entities) {
            setId(entity, currentId++)
        }
    }
}
