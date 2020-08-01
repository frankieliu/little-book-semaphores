from threading import Thread, Semaphore
import time
from random import randint

class PersonA(Thread):
    def __init__(self,s1,s2):
        self.s1 = s1
        self.s2 = s2
        super().__init__()
    def run(self):
        time.sleep(1e-3*randint(1,10))
        print("a1")
        self.s1.release()
        self.s2.acquire()
        print("a2")

class PersonB(Thread):
    def __init__(self,s1,s2):
        self.s1 = s1
        self.s2 = s2
        super().__init__()
    def run(self):
        time.sleep(1e-3*randint(1,10))
        print("b1")
        self.s2.release()
        self.s1.acquire()
        print("b2")

def main():
    s1 = Semaphore(0)
    s2 = Semaphore(0)
    pA = PersonA(s1,s2)
    pB = PersonB(s1,s2)
    pA.start()
    pB.start()
    pA.join()
    pB.join()


main()
