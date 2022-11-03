package io.runebox.deobfuscator.util

inline fun <T> MutableIterable<T>.iterate(block: MutableIteratorBlock<T>.(T) -> Unit) {
    val itr = MutableIteratorBlock(this.iterator())
    while(itr.hasNext()) {
        val it = itr.next()
        itr.block(it)
    }
}

class MutableIteratorBlock<T>(private val itr: MutableIterator<T>) : MutableIterator<T> by itr