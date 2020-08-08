from threading import Thread, Semaphore
import time
from random import randint

class Leader(Thread):
    def __init__(self,i,kvargs):
        self.__dict__.update(kvargs)
        super().__init__(name=i)

    def run(self):
        time.sleep(1e-3*randint(1,10))
        print(f"leader {self.name} enter")
        self.m.acquire()
        self.leaders[0] += 1
        if self.followers[0] > 0:
            self.followerQueue.release()
        else:
            self.m.release()
            self.leaderQueue.acquire()
        self.leaders[0] -= 1
        print(f"leader {self.name} dancing")
        self.rendezvous.acquire()
        self.m.release()

class Follower(Thread):
    def __init__(self,i,kvargs):
        self.__dict__.update(kvargs)
        super().__init__(name=i)

    def run(self):
        time.sleep(1e-3*randint(1,10))
        print(f"follower {self.name} enter")
        self.m.acquire()
        self.followers[0] += 1
        if self.leaders[0] > 0:
            self.leaderQueue.release()
        else:
            self.m.release()
            self.followerQueue.acquire()
        self.followers[0] -= 1
        print(f"follower {self.name} dancing")
        self.rendezvous.release()

def main():
    nthreads = 10
    m = Semaphore(1)
    followers = [0]
    followerQueue = Semaphore(0)
    leaders = [0]
    leaderQueue = Semaphore(0)
    rendezvous = Semaphore(0)
    parameters = {
            "m": m,
            "followers": followers,
            "followerQueue": followerQueue,
            "leaders": leaders,
            "leaderQueue": leaderQueue,
            "rendezvous": rendezvous }
    thr = []
    for i in range(nthreads):
        thr.append(Follower(i+1, parameters))
        thr.append(Leader(i+1, parameters))
    for t in thr:
        t.start()
    for t in thr:
        t.join()

main()
