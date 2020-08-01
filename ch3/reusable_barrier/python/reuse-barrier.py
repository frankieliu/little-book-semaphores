from threading import Thread, Semaphore
import time
from random import randint

class Person(Thread):
    def __init__(self,i,m,s1,s2,count,numthreads,niter):
        self.s1 = s1
        self.s2 = s2
        self.m = m
        self.count = count
        self.count.i = 0
        self.numthreads = numthreads
        self.niter = niter
        super().__init__(name=i)

    def run(self):
        for i in range(self.niter):
            time.sleep(1e-3*randint(1,10))

            print(f"{self.name} {i+1} rendez")

            # phase 1
            self.m.acquire()
            self.count.i += 1
            if self.count.i == self.numthreads:
                self.s2.acquire()
                self.s1.release()
            self.m.release()
            self.s1.acquire()
            self.s1.release()

            print(f"{self.name} {i+1} critical section")

            # phase 2
            self.m.acquire()
            self.count.i -= 1
            if self.count.i == 0:
                self.s1.acquire()
                self.s2.release()
            self.m.release()
            self.s2.acquire()
            self.s2.release()

class Count():
    pass

def main():
    niter = 10
    nthreads = 10
    m = Semaphore(1)
    s1 = Semaphore(0)
    s2 = Semaphore(1)
    count = Count()
    thr = []
    for i in range(nthreads):
        thr.append(Person(i+1, m, s1, s2, count, nthreads, niter))
    for t in thr:
        t.start()
    for t in thr:
        t.join()

main()
