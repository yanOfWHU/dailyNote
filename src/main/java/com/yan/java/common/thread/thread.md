interface Runnable
  void run() 无返回值
interface Callable
  T call() 有返回值; Callable 一般结合 ExecutorService 使用
interface ExecutorService
  Future<T> submit(Callable<T> task)
  Future<T> submit(Runnable task, T result)
  Future<?> submit(Runnable task)
interface Future
  对具体的 Runnable 或者 Callable 任务的执行结果进行取消，查询是否成功，获取结果。
  必要的时候可以通过 get 获取执行结果，但是该方法会阻塞任务直到返回结果。
  boolean cancel(boolean mayInterruptIfRunning)
    取消任务，取消成功返回 true，取消失败返回 false。
    参数的作用表示是否取消正在执行却没有执行完毕的任务。
    设置为 true，表示可以取消正在执行的任务。
    case1：任务已经完成，无论参数是 true 还是 false，都返回false，因为无法取消。
    case2：任务在执行，参数 true，则返回 true，表示可以取消。
    case3：任务在执行，参数 false，则返回 false，因为当前任务正在运行
    case3：任务还没执行，无论参数是 true 还是 false，都返回 true，因为没执行，就可以取消。
  boolean isCanceled()
  boolean isDone()
  V get()
  V get(long timeout, TimeUnit unit)
interface RunnableFuture<V> extends Runnable, Future<V>
  run()
  因为该接口继承了 Runnable 以及 Future 接口，则表示既可以作为 Runnable 被执行，又可以作为 Future 获取返回结果。
class FutureTask<V> implements RunnableFuture<V>
  FutureTask(Callable<V> callable)
  FutureTask(Runnable runnable, V result)
  FutureTask 的状态：
  未启动: new FutureTask(...)
  已启动: futureTaskInstance.run()
  已完成：
      正常结束，运行完毕
      被取消，cancel(因为实现了 Future 接口，所以可以 cancel)
      异常结束

