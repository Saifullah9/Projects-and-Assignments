#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <semaphore.h>
#include <time.h>
#include <sys/time.h>
#include <pthread.h>
#include <sys/resource.h>
#include <string.h>



static sem_t mutex;             //Semaphore for readCount
static sem_t mutex_rw;          //Semaphore for reader/writer access
static int readCount = 0;       //Number of readers accessing target
static int target = 0;          //Target value to read and write
static int currentReader = 0;   //Index for current reader
static int currentWriter = 0;   //Index for current wirter
static int numReader = 500;		// Number of reader threads
static int numWriter = 10;		// Number of writer threads
static sem_t serviceQueue;		// Preserves the ordering of requests 
static int writeCount = 0; 		//Number of writers accessing target which I added to solve the starvation problem

static float readerVal[500], writerVal[10]; //reader and writer waiting times
/*
Initializing methods
*/

float minimum(float array[], size_t size);
float maximum(float array[], size_t size);
float average(float array[], size_t size);

static void *reader(void * args)            //Reader function
{
  int loops = *((int *) args);              //args used to determine number of times to loop
  struct timeval tv;                        //Used to access current time
  time_t dTime, timeIn, timeOut;            //used to calculate timing
  dTime = 0;      							//Initializing time values
  timeIn = 0;
  timeOut = 0;
  int i;

  for(i = 0; i < loops; i++){
    int r = rand();                           //create random variable used for the usleep method
    gettimeofday(&tv, NULL);                  //getting current time for analysis
    timeIn = tv.tv_sec*1000000 + tv.tv_usec;
    if(sem_wait(&mutex) == -1){                //Making sure no other reader is accessing the readCount variable
      exit(2);
	}	  
    
	if((writeCount > 0) || (readCount == 0)){
		if(sem_post(&mutex) == -1){              
		exit(2);
		}
		 if(sem_wait(&mutex_rw)==-1)             //Waiting for the critical section, time is also updated
      {
        exit(2);
      }
	  if(sem_wait(&mutex) == -1){                //Making sure no other reader 
      exit(2);
	  }
	
	}
	
	/* 
	Getting the time after waiting and updating it, then reseting the time values
	*/
	gettimeofday(&tv, NULL);
    timeOut = tv.tv_sec*1000000 + tv.tv_usec; 
    dTime = dTime + (timeOut - timeIn);       
    timeOut = 0;                              
    timeIn = 0;
	readCount++;                              //increment the number of readers
	
    if(sem_post(&mutex) == -1){              //When the readCount is updated, signal semaphore
      exit(2);
	}
	
    printf("Current target value %d\n. There are currently %d readers\n", target, readCount);

    gettimeofday(&tv, NULL);            //update timing and check that access to decrement readCount
    timeIn = tv.tv_sec*1000000 + tv.tv_usec;  
    if(sem_wait(&mutex) == -1){
      exit(2);
	}
    gettimeofday(&tv, NULL);
    timeOut = tv.tv_sec*1000000 + tv.tv_usec;
    dTime = dTime + (timeOut-timeIn);
    timeOut = 0;
    timeIn = 0;

    readCount--;                 //decrement readCount
    if(readCount == 0)           //If reader is last to exit, check to make sure no signal to allow writer to access target
    {                                         
      if(sem_post(&mutex_rw) == -1)
      {
        exit(2);
      }

    }
    if(sem_post(&mutex) == -1)                //signal that the reader has finished accessing the readCount variable
      exit(2);                              

    usleep((float)(r%100000));                   //sleep for random time between 0 to 100ms
  }
  readerVal[currentReader] = dTime;           //store timing information and update currentReader
  currentReader++;                            
}

static void *writer(void * args)              //writer function
{
  int loops = *((int *) args);                //args used to determine number of times to loop
  int temp;                                   //Used to temp store target
  struct timeval tv;                          //used to calculate and hold timing
  time_t dTime, timeIn, timeOut;

  dTime = 0;              //initiate timing values
  timeIn = 0;
  timeOut = 0;

  int r = rand();         //create random number for the usleep method.

  int i;
  
  for (i = 0; i < loops; i++)
  {
    gettimeofday(&tv, NULL);                  //start timing anaylsis
    timeIn = tv.tv_sec*1000000 + tv.tv_usec;
	 if (sem_wait(&mutex) == -1){           
	 exit(2);
	 }
	 writeCount++;
	 
	 if(sem_post(&mutex) == -1){             //signal that write is complete
	 exit(2);
	 }
	 
    if (sem_wait(&mutex_rw) == -1)            //check access to critical section
      exit(2);
    gettimeofday(&tv, NULL);                  //capture wating time
    timeOut = tv.tv_sec*1000000 + tv.tv_usec;
    dTime = dTime + (timeOut - timeIn);       //update waiting time
    timeOut = 0;                              //reset timing values
    timeIn = 0;

    printf("writing to target\n");            //write to target value
    temp = target;
    temp = temp+10;
    target = temp;
	
	 if (sem_wait(&mutex) == -1){           
	 exit(2);
	 }
	writeCount--;
	
	if(sem_post(&mutex) == -1){  
	 exit(2);
	 }
    if(sem_post(&mutex_rw) == -1)             //signal that write is complete
      exit(2);
    usleep((float)(r%100000));                   //sleep for random time between 0 and 100 ms
  }
  writerVal[currentWriter] = dTime;           //store timing information
  currentWriter++;                            //update current writer
}

int main(int argc, char *argv[]){
	
  pthread_t readers[numReader],writers[numWriter];         //Thread arrays initialization
  int n;
  int w;
  int r;  
  int Wloop = atoi(argv[1]);
  int Rloop = atoi(argv[2]);
 
				
  float  readMaximum, readMinimum, readAverage, writeMaximum, writeMinimum, writeAverage; //hold timing info

  srand(time(NULL));            //seed random number generator

  if(sem_init(&mutex,0,1) == -1)          //initiating semaphores
  {
    printf("Error initiating semaphore exiting...\n");
    exit(1);
  }

  if(sem_init(&mutex_rw,0,1) == -1)
{
    printf("Error initiating semaphore exiting...\n");
    exit(1);
  }

  for (w = 0; w < numWriter; w++){   //create writer threads
    printf("creating writer thread %d \n",w);
    n = pthread_create(&writers[w], NULL, &writer, &Wloop);
    if(n !=0){
		
      printf("Error creating writer %d exiting...\n",w);
      exit(1);
    }

  }

  for (r = 0; r < numReader; r++){   //create reader threads
    printf("creating reader thread\n",r);
    n = pthread_create(&readers[r], NULL, &reader, &Rloop);
    if(n !=0){
	 // int x = pthread_attr_getstacksize();
	 printf("error is : %d \n",n);
      printf("Error creating reader %d exiting...\n",r);		
      exit(1);
    }

  }

  for (w = 0; w < numWriter; w++){  //detaching writer threads
    n = pthread_join(writers[w], NULL);
    if (n != 0) {
      printf("Error, creating threads\n");
      exit(1);
    }

  }

  for (r = 0; r < numReader; r++){  //detaching reader threads

    n = pthread_join(readers[r], NULL);
    if (n != 0) {
      printf("Error, creating threads\n");
	  
      exit(1);
    }

  }

/*
Here we are getting the maximum,minimum and average values for both readers and writers.
*/
  readMaximum = maximum(readerVal, numReader);
  readMinimum = minimum(readerVal, numReader);
  readAverage = average(readerVal, numReader);

  writeMaximum = maximum(writerVal, numWriter);
  writeMinimum = minimum(writerVal, numWriter);
  writeAverage = average(writerVal, numWriter);
  
  printf("Minimum waiting time for readers is: %f milliseconds\n", readMinimum/1000);
  printf("Maximum waiting time for readers is: %f milliseconds\n", readMaximum/1000);
  printf("Average waiting time for readers is: %f milliseconds\n", readAverage/1000);
  printf("Minimum waiting time for the writers is: %f milliseconds\n", writeMinimum/1000);
  printf("Maximum waiting time for the writers is: %f milliseconds\n", writeMaximum/1000);
  printf("Average waiting time for the writers is: %f milliseconds\n", writeAverage/1000);

}

/*
This method returns the minimum value of an array of floats
*/
float minimum(float array[], size_t size){
  float min = array[0];
  int i;
  for(i = 1; i < size; i++)
  {
    if(min > array[i])
      min = array[i];
  }

  return min;
}

/*
This method returns the maximum value of an array of floats.
*/
float maximum(float array[], size_t size) //calculate max value of an array of floats
{
  float max = array[0];
  int i;
  for(i = 1; i < size; i++)
  {
    if(max < array[i])
      max = array[i];
  }

  return max;
}

/*
This method returns the average of an array of floats. 
*/
float average(float array[], size_t size){
  float average = 0;
  float sum = 0;
  int i;
  for(i = 0; i < size; i++){
    sum = sum + array[i];
  }
  average = sum /((float) size);
  return average;
}

