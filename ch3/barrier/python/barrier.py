from threading import Thread, Semaphore
import time
from random import randint

class Person(Thread):
    def __init__(self,i,m,s,count,numthreads):
        self.s = s
        self.m = m
        self.count = count
        self.count.i = 0
        self.numthreads = numthreads
        super().__init__(name=i)

    def run(self):
        time.sleep(1e-3*randint(1,10))

        print(f"{self.name} rendez")

        # barrier
        self.m.acquire()
        self.count.i += 1
        if self.count.i == self.numthreads:
            self.s.release()
        self.m.release()
        self.s.acquire()
        self.s.release()

        print(f"{self.name} critical section")

class Count():
    pass

def main():
    nthreads = 10
    m = Semaphore(1)
    s = Semaphore(0)
    count = Count()
    thr = []
    for i in range(nthreads):
        thr.append(Person(i+1, m, s, count, nthreads))
    for t in thr:
        t.start()
    for t in thr:
        t.join()

main()
