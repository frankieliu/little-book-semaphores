#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>

sem_t aArrived, bArrived;

void a1()
{
    sleep(1);
	printf(" processing a1\n");
}

void b1()
{
    sleep(1);
	printf(" processing b1\n");
}

void a2()
{
    sleep(1);
	printf(" processing a2\n");
}

void b2()
{
    sleep(1);
	printf(" processing b2\n");
}

void* threadA(void* p)
{
	
	// perform a1
	a1();
	sem_post(&aArrived);
	sem_wait(&bArrived);
	// perform a2
	a2();	
	return NULL;
}

void* threadB(void* p)
{
	
	// perform b1
	b1();
	sem_post(&bArrived);
	sem_wait(&aArrived);
	// perform b2
	b2();
	return NULL;
}

int main()
{
	pthread_t thread1, thread2;
    char *message1 = "Thread 1";
    char *message2 = "Thread 2";
	sem_init(&bArrived, 0, 0);
	sem_init(&aArrived, 0, 0);

	pthread_create(&thread1, NULL, threadA, message1);
	pthread_create(&thread2, NULL, threadB, message2);

	pthread_join(thread1, NULL);
    pthread_join(thread2, NULL);
	return 0;
}











