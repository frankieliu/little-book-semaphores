#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>

pthread_cond_t turn = PTHREAD_COND_INITIALIZER;
pthread_mutex_t m = PTHREAD_MUTEX_INITIALIZER;

int writers=0, writing=0, reading=0;

void pR(int i)
{
	printf(" Reader  %d \n", i);
}

void pW(int i)
{
	printf(" Writer %d \n", i);
}
 

void* threadR(void* p)
{
	pthread_mutex_lock(&m);
	while ( writers > 0 )
		pthread_cond_wait (&turn, &m);

	reading++;
	pthread_mutex_unlock(&m);

        /* CS start */

	pR( reading );
	sleep (1);

        /* CS end */
	pthread_mutex_lock(&m);
	reading--;
	pthread_cond_broadcast (&turn);
	pthread_mutex_unlock(&m);

	return NULL;
}

void* threadW(void* p)
{
	pthread_mutex_lock(&m);
        writers++;

	while ( writing || reading )
		pthread_cond_wait (&turn, &m);

	writing++;
	pthread_mutex_unlock(&m);

        /* CS start */
	pW( writing );
	sleep (1);
        /* CS end */

	pthread_mutex_lock(&m);
        writers--;
	writing--;
	
	pthread_cond_broadcast (&turn);
	pthread_mutex_unlock(&m);
	
	return NULL;
}

int main()
{
	pthread_t tR1, tR2,tR3, tR4, tR5, tR6, tR7, tR8, tW1, tW2,tW3, tW4, tW5;

	pthread_create(&tR1, NULL, threadR, NULL);
	pthread_create(&tR2, NULL, threadW, NULL);
	pthread_create(&tR3, NULL, threadR, NULL);
	pthread_create(&tR4, NULL, threadR, NULL);
	pthread_create(&tR5, NULL, threadR, NULL);
	pthread_create(&tR6, NULL, threadR, NULL);
	pthread_create(&tR7, NULL, threadR, NULL);
	pthread_create(&tR8, NULL, threadR, NULL);

	pthread_create(&tW1, NULL, threadW, NULL);
        printf (" Created Wr 1 \n");
	pthread_create(&tW2, NULL, threadW, NULL);
        printf (" Created Wr 2 \n");
	pthread_create(&tW3, NULL, threadW, NULL);
        printf (" Created Wr 3 \n");

	pthread_join(tR1, NULL);
	pthread_join(tR2, NULL );
	pthread_join(tR3, NULL );
	pthread_join(tR4, NULL );
	pthread_join(tR5, NULL );
	pthread_join(tR6, NULL );
	pthread_join(tR7, NULL );
	pthread_join(tR8, NULL );

	pthread_join(tW1, NULL );
        printf (" Terminated Wr 1 \n");
	pthread_join(tW2, NULL );
        printf (" Terminated Wr 2 \n");
	pthread_join(tW3, NULL );
        printf (" Terminated Wr 3 \n");

	return 0;
}
