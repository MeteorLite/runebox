package io.runebox.deobfuscator.util

fun <T> MutableIterable<T>.iterate(block: MutableIterator<T>.(T) -> Unit) {
    val itr = this.iterator()
    while(itr.hasNext()) {
        itr.block(itr.next())
    }
}