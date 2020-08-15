from threading import Thread, Semaphore
from time import sleep
from random import randint

def randtime():
    sleep(randint(100,300)*1e-3)

class reader(Thread):
    def __init__(self,i,kvargs):
        super().__init__(name=str(i))
        self.__dict__.update(kvargs)
    def run(self):
        randtime()
        self.wait_room.acquire()
        self.wait_room.release()
        self.mutex.acquire()
        if self.num_reading[0] == 0:
            self.library.acquire()
        self.num_reading[0] += 1
        print(f"Reader {self.name} {self.num_reading[0]}")
        self.mutex.release()
        sleep(500*1e-3)
        self.mutex.acquire()
        if self.num_reading[0] == 1:
            print(f"Reader {self.name} last exit")
            self.library.release()
        self.num_reading[0] -= 1
        self.mutex.release()

class writer(Thread):
    def __init__(self,i,kvargs):
        super().__init__(name=str(i))
        self.__dict__.update(kvargs)
    def run(self):
        randtime()
        self.wait_room.acquire()
        self.library.acquire()
        self.wait_room.release()
        print(f"Writer {self.name}")
        self.library.release()

def main():
    nreaders = 20
    nwriters = 20
    d = {
          "wait_room": Semaphore(1),
          "library": Semaphore(1),
          "mutex": Semaphore(1),
          "num_reading": [0],
        }
    thread_list = []
    for i in range(nreaders):
        thread_list.append(reader(i,d))
        thread_list.append(writer(i,d))
    for t in thread_list:
        t.start()
    for t in thread_list:
        t.join()

main()
