from threading import Thread, Semaphore
import time
from random import randint
from collections import deque

class Producer(Thread):
    def __init__(self,i,kvargs):
        self.__dict__.update(kvargs)
        super().__init__(name=f"Pr {i}")

    def run(self):
        time.sleep(1e-4*randint(1,10))
        for e in self.e:
            event = f"{self.name} {e}"
            self.m.acquire()
            self.buffer.append(event)
            print(event)
            self.m.release()
            self.q.release()

class Consumer(Thread):
    def __init__(self,i,kvargs):
        self.__dict__.update(kvargs)
        super().__init__(name=f"Cr {i}")

    def run(self):
        terminate = False
        while not terminate:
            time.sleep(1e-4*randint(1,10))
            self.q.acquire()
            self.m.acquire()
            event = self.buffer.popleft()
            if event == "EOF":
                terminate = True
            print(f"{self.name} received {event}")
            self.m.release()

def main():
    events = range(10)
    m = Semaphore(1)
    buf = deque()
    q = Semaphore(0)
    parameters = {
            "e": events,
            "m": m,
            "buffer": buf,
            "q": q,
            }
    nthreads = 5
    pthr,cthr = [],[]
    for i in range(nthreads):
        pthr.append(Producer(i+1, parameters))
        cthr.append(Consumer(i+1, parameters))
    for t in cthr:
        t.start()
    for t in pthr:
        t.start()
    for t in pthr:
        t.join()
    for t in cthr:
        m.acquire()
        buf.append('EOF')
        m.release()
        q.release()

main()
