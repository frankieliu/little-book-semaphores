from threading import Thread, Semaphore
from random import randint
from time import sleep

def randtime():
    sleep(randint(1,300)*1e-3)

class consumer(Thread):
    def __init__(self,i,kvargs):
        super().__init__(name=str(i)) 
        self.__dict__.update(kvargs)
    
    def run(self):
        for _ in range(self.ntimes):
            randtime()
            self.full.acquire()
            self.mutex.acquire()
            self.idx[0] -= 1
            el = self.buf[self.idx[0]]
            self.mutex.release()
            print(f"Con {self.name} <- {el}")
            self.empty.release()

class producer(Thread):
    def __init__(self,i,kvargs):
        super().__init__(name=str(i)) 
        self.__dict__.update(kvargs)

    def run(self):
        for i in range(self.ntimes):
            randtime()
            self.empty.acquire()
            self.mutex.acquire()
            el = int(self.name)*100 + i
            self.buf[self.idx[0]] = el 
            self.idx[0] += 1
            self.mutex.release()
            print(f"Pro {self.name} -> {el}")
            self.full.release()

def main():
    ncons = nprod = 10
    buf_size = 4
    d = { 
        "buf_size": buf_size,
        "ntimes": 20,
        "idx": [0],
        "buf": [0]*buf_size,
        "full": Semaphore(0),
        "empty": Semaphore(buf_size),
        "mutex": Semaphore(1)
        }
    thr_list = [] 
    for i in range(ncons):
        thr_list.append(consumer(i,d))
        thr_list.append(producer(i,d))
    for t in thr_list:
        t.start()
    for t in thr_list:
        t.join()


main()

