#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>

sem_t aArrived, bArrived;

void a1()
{
	printf(" processing a1\n");
}

void a2()
{
	printf(" processing a2\n");
}

void* threadA(void* p)
{
	pthread_create();
	sem_init(&aArrived, 0, 0);
	
	// perform a1
	a1();
	aArrived.sem_post();
	bArrived.sem_wait();
	// perform a2
	a2();	
	return NULL;
}

void* threadB(void* p)
{
	sem_init(&bArrived, 0, 0);
	
	// perform b1
	b1();
	bArrived.sem_post();
	aArrived.sem_wait();
	// perform b2
	b2();
	return NULL;
}

int main()
{
	pthread_t threadA, threadB;

	pthread_create(&thread1, NULL, threadA, NULL);
	pthread_create(&thread2, NULL, threadB, NULL);

	pthread_join();
	return 0;
}











