package son.ysy.creator.keys

import son.ysy.creator.annotations.KeyCreator

@KeyCreator(keys = ["key1", "key2"])
class TestKeyCreator {

    @KeyCreator(keys = ["key2"])
    class InnerClass {

    }

    init {
        val key1 = TestKeyCreatorKeys.key1
    }
}