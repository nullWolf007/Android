## Heap Memory and Stack Memory
---

### Java Heap Memoory
*   Heap Memory用于java运行环境中用来分配给对象和JRE类的内存，GC是针对Heap而言的。Heap在java中是使用new创建的对象的内存。

### Java Stack Memory
*   Stack内存是相对于线程Thread而言的，它保存线程方法中短期存在的变量值和对Heap中对象的引用，Stack总是FILO方式的，Stack是随用随销的

### Heap和Stack的主要区别
1. Heap作为应用的 共享内存，而Stack只是为某个执行线程所使用
2. 当通过new方式创建一个对象是，它会被存放在heap中，而stack只会保存一个该对象的引用，stack仅仅保存临时变量和对heap中对象的引用
3. 存储在heap中的对象是全局可访问的，而stack只能被特定的线程访问
4. stack内存管理机制是FILO，而heap使用分带式内存管理，分成新生代，老年代，持久代三个部分([heap详细的内存机制请点击这里](https://github.com/nullWolf007/android/blob/master/%E5%AE%89%E5%8D%93%20GC.md))
5. stack中数据的大小和生存周期是固定的，而heap比较灵活
6. stack相较于heap内存空间比较小。由于内存分配策略LIFO，stack的存储速度会比heap快
7. 利用-Xms和-Xmx来指明JVM的堆初始空间和最大空间，利用-Xss来定义栈空间大小
8. 当栈空间满了，Java运行时会抛出 java.lang.StackOverFlowError ；堆空间满了，抛出的 java.lang.OutOfMemoryError: Java Heap Space

### 数据类型：基本数据类型和引用数据类型

<img src="https://github.com/nullWolf007/images/blob/master/Java/heapStack/15165530-8a570626bf3741a1b4937759a89a5a93.png" width="630" height="360" />


### 堆和栈

#### 栈是运行时的单位，堆是存储时的单位
#### 为什么要把堆和栈分开来？栈中不也是可以存储数据么？
1. 从软件设计思想来说，堆代表数据，栈代表逻辑处理。这样分开，符合模块化的思想，使其更加清晰
2. 堆和栈的分离，使得多个栈可以共享一个堆的内容(也就是多个线程访问一个对象)，这样做一方面节省了空间，另一方面这是一种有效的数据交互方式
3. 堆比较灵活，堆中的对象可以动态增长的，对于栈只需要记录堆中对象的引用即可
4. 面向对象就是堆和栈的完美结合，堆中存放的就是对象的属性(也就是数据)，栈中存放对象的行为(方法)(也就是运行逻辑)
5. 栈中存放的基本数据类型和堆对象的引用。基本数据类型比较小，且是固定的，不会动态增长，所以放在栈中存放

### 对象引用类型：强引用，软引用，弱引用，虚引用
[详细内容请点击这里](https://github.com/nullWolf007/android/blob/master/java/%E5%9B%9B%E7%A7%8D%E5%BC%95%E7%94%A8%E7%B1%BB%E5%9E%8B.md)
