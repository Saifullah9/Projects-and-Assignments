#include "sfs_api.h"
#include "disk_emu.h"
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define BLOCKS_SIZE 1024
#define NUM_OF_BLOCKS 25000
#define INODE_ENTRY_TABLE_LENGTH 14 // This is the line that I changed TA Thein (from 15 to 14)
#define ROOT_DIRECT 0
#define NUM_INODE_ENTRYS 200
#define Directory_LENGTH 200
#define Directory_BLOCKS 5
#define FILEDISCRIPTOR_TABLE_LENGTH 20
#define INODE_ENTRY_TABLE_BLK 1
#define FREE_BITMAP_LENGTH 5
#define FREE_BITMAP_BLK 24994
#define Directory_BLK_INDEX 24989



typedef struct INODE_ENTRY
{
  int mode;
  int link_Count;
  int size;
  int pointer[12];
  int indirect_pointer;
} INODE_ENTRY;

typedef struct Directory
{
  char file_name[21];
  int INODE_ENTRY;
} Directory;

typedef struct file_Discriptor_Entry
{
  int INODE_ENTRY;
  int read_pointer;
  int write_pointer;
} file_Discriptor_Entry;

int s_bx[256];
unsigned char bitmap[3125];

int INT_SIZE;
int DI;

INODE_ENTRY INODE_ENTRYTable[NUM_INODE_ENTRYS];
Directory dir[Directory_LENGTH];
file_Discriptor_Entry fileDiscripto[FILEDISCRIPTOR_TABLE_LENGTH];

int addINODE_ENTRYBlock(int block, INODE_ENTRY *INODE_ENTRY);
int usedBlock_setter(int block);
int nextfree_block();
int sfs_getfilesize(char *path);
int w_bit_map(unsigned char *bitmap);
int r_bit_map(unsigned char *bitmap);
int w_INODE_ENTRYTable(INODE_ENTRY *i_table);
int r_INODE_ENTRYTable(INODE_ENTRY *i_table);
int getBlocks(int block, INODE_ENTRY INODE_ENTRY);
int free_blocks(int block);
int DirectoryFinder(char *file_name);
int file_Discriptor_EntryFinder(int INODE_ENTRY);
int nextEmptyDirectory();
int nextEmptyINODE_ENTRY();
int nextEmptyfileDiscripto();
int sfs_getnextfilename(char *fname);
int write_super_block(int *s_bx);
int sfs_fcreate(char *file_name);
int write_Directory(Directory *dir);
int read_Directory(Directory *dir);
int read_super_block(int *s_bx);
char * currOpenFile;
int addINODE_ENTRYBlock(int block, INODE_ENTRY *INODE_ENTRY){
  for(int i = 0; i < 12; i ++){
    if(!((INODE_ENTRY->pointer[i]) > 0)){
      INODE_ENTRY->pointer[i] = block; 
      return 0;
    }
  }

  int inblock = 1024/INT_SIZE; 
  int indirect_pointer [inblock]; 
  memset(indirect_pointer, 0, inblock *INT_SIZE); 
  int indirect_pointerBlock; 
  if(!((indirect_pointerBlock = INODE_ENTRY->indirect_pointer)> 0)){
    if ((indirect_pointerBlock = nextfree_block())<0){
      printf("can't find new free block");
      return -1;
    }
    memset(indirect_pointer, 0, sizeof(int)*inblock);
    indirect_pointer[0] = block;
    INODE_ENTRY->indirect_pointer = indirect_pointerBlock; 
  }else{
    if(read_blocks(indirect_pointerBlock, 1, indirect_pointer)< 0){
      printf("can't read indirect pointer block"); 
      return -1;
    }
    int i ;
    for(i = 0; i < inblock; i++){
      if (indirect_pointer[i]==0){
        indirect_pointer[i] = block;
        break;
      }
    }
    if(i == inblock){ 
      return -1;
    }
  }
  if(write_blocks(indirect_pointerBlock, 1 , indirect_pointer)< 0){
    printf(" can't write from indirect point to block"); 
    return -1;

  }

  if (w_INODE_ENTRYTable(INODE_ENTRYTable)< 0){
    printf("can't write to INODE_ENTRYTable"); 
    return -1;
  }
  return 0;
}


int usedBlock_setter(int block){
  if (block < 16 || block >= 24995){
    printf(" block selected is not a data block"); 
    return -1;
  }

  int bitmapI = block /8 ; 
  unsigned char bitNum = block % 8; 
  unsigned char bitVal = 1 << bitNum; 
  unsigned char val = bitmap[bitmapI];

  if(!((val >> bitNum)& 1)){
    printf(" block is already in use"); 
    return -1;
  }

  unsigned char newVal = val - bitVal; 
  bitmap[bitmapI] = newVal; 

  if(w_bit_map(bitmap) < 0){
    return -1; 
  }
  return 0; 
}



int nextfree_block() {
  for(int i = 2; i < 3125; i++){
    if(bitmap[i]){
      for(int j = 0; j < 8; j++){
        if((bitmap[i]>> j)&1){
          int found = 8*i+j;
          if(usedBlock_setter(found)<0){
            printf("can't set found bit as used"); 
          }
          return found;
        }
      }
      
    }
  }
  printf(" no free block found!"); 
  return -1;
}

int getBlocks(int block, INODE_ENTRY INODE_ENTRY)
{
  int inBlock = 1024 / INT_SIZE;
  int blockI;
  if (block >= 0 && block < 12)
  {
    blockI = INODE_ENTRY.pointer[block];
  }
  else if (block < inBlock + 12)
  {
    int indBlock = INODE_ENTRY.indirect_pointer;
    int indirect_pointer[inBlock];
    if (read_blocks(indBlock, 1, indirect_pointer) < 0)
    {
      printf("can't read indirect pointers");
      return -1;
    }
    blockI = indirect_pointer[block - 12];
  }
  else
  {
    return -1;
  }
  return blockI;
}
int free_blocks(int block)
{
  if (block < 16 || block >= 24995)
  {
    printf(" invalid block number");
    return -1;
  }

  int bitmapIndex = block / 8;
  unsigned char bitNum = block % 8;
  unsigned char bitVal = 1 << bitNum;

  unsigned char val = bitmap[bitmapIndex];

  if (((val >> bitNum) & 1))
  {
    printf(" Memory is already free");
    return -1;
  }

  unsigned char new_char = val | bitVal;
  bitmap[bitmapIndex] = new_char;
  if (w_bit_map(bitmap) < 0)
  {
    return -1;
  }
  return 0;
}
int DirectoryFinder(char *file_name)
{
  for (int i = 0; i < Directory_LENGTH; i++)
  {
    if (dir[i].INODE_ENTRY < 1)
    {
      continue;
    }
    if (strcmp(dir[i].file_name, file_name))
    {
      return i;
    }
  }
  
  return -1;
}
int file_Discriptor_EntryFinder(int INODE_ENTRY)
{
  for (int i = 0; i < FILEDISCRIPTOR_TABLE_LENGTH; i++)
  {

    if (fileDiscripto[i].INODE_ENTRY < 1)
    {
      continue;
    }
    if (fileDiscripto[i].INODE_ENTRY == INODE_ENTRY)
    {
      return i;
    }
  }
  return -1;
}
int nextEmptyDirectory()
{
  for (int i = 0; i < Directory_LENGTH; i++)
  {
    if (dir[i].INODE_ENTRY == 0)
    {
      return i;
    }
  }
  printf(" dir is full! \n");
  return -1;
}
int nextEmptyINODE_ENTRY()
{
  for (int i = 0; i < NUM_INODE_ENTRYS; i++)
  {
    if (INODE_ENTRYTable[i].mode == 0)
    {
      return i;
    }
  }
  
  
  
  
  printf("INODE_ENTRY Table is full!\n");
  return -1;
}
int nextEmptyfileDiscripto()
{
  for (int i = 0; i < FILEDISCRIPTOR_TABLE_LENGTH; i++)
  {
    if (fileDiscripto[i].INODE_ENTRY == 0)
    {
      return i;
    }
  }
  return -1;
}

int sfs_getnextfilename(char *fname)
{
  if (dir[DI].INODE_ENTRY < 1)
  {
    printf("Last file in Directory");
    DI = 0;
    return 0;
  }
  char *name = dir[DI++].file_name;
  strcpy(fname, name);
  return 1;
}
int sfs_fcreate(char *file_name)
{
  if (strlen(file_name) > 20)
  {
    printf("file name too long");
    return -1;
  }

  int Directory_newIndex = -1;
  if ((Directory_newIndex = nextEmptyDirectory()) < 0)
  {
    printf(" Directory has no free space!\n");
    return -1;
  }
  int new_INODE_ENTRYIndex = -1;
  if ((new_INODE_ENTRYIndex = nextEmptyINODE_ENTRY()) < 0)
  {
    printf("free node entry!");
    memset(&dir[Directory_newIndex], 0, sizeof(Directory));
    return -1;
  }
  INODE_ENTRY new_INODE_ENTRY = {.mode = 1};

  INODE_ENTRYTable[new_INODE_ENTRYIndex] = new_INODE_ENTRY;

  Directory new_dir = {.INODE_ENTRY = new_INODE_ENTRYIndex};
  strcpy(new_dir.file_name, file_name);
  dir[Directory_newIndex] = new_dir;

  if (write_Directory(dir) < 0)
  {
    printf("can't write to dir");
    return -1;
  }
  if (sizeof(INODE_ENTRYTable) / 1024 > INODE_ENTRY_TABLE_LENGTH)
  {
    printf("INODE_ENTRY table is too Large!\n");
    return -1;
  }
  if ((write_blocks(INODE_ENTRY_TABLE_BLK, INODE_ENTRY_TABLE_BLK, INODE_ENTRYTable)) < 0)
  {
    printf("can't write INODE_ENTRY table to drive");
    return -1;
  }
  return new_INODE_ENTRYIndex;
}
int sfs_getfilesize(char *path)
{
  int di;
  if ((di = DirectoryFinder(path)) < 0)
  {
    printf("file not found in Directory");
    return -1;
  }

  int INODE_ENTRY = dir[di].INODE_ENTRY;
  if (INODE_ENTRY < 1 || INODE_ENTRY > NUM_INODE_ENTRYS)
  {
    printf("INODE_ENTRY index is incorrect");
    return -1;
  }
  int filesize = INODE_ENTRYTable[INODE_ENTRY].size;
  return filesize;
}

int w_bit_map(unsigned char *bitmap)
{
  if (write_blocks(FREE_BITMAP_BLK, FREE_BITMAP_LENGTH, bitmap) < 0)
  {
    printf("Error write bitmap to disk\n");
    return -1;
  }
  return 0;
}

int r_bit_map(unsigned char *bitmap)
{
  if (read_blocks(FREE_BITMAP_BLK, FREE_BITMAP_LENGTH, bitmap) < 0)
  {
    printf("Error read bitmap from disk\n");
    return -1;
  }
  return 0;
}

int w_INODE_ENTRYTable(INODE_ENTRY *i_table)
{
  if (write_blocks(INODE_ENTRY_TABLE_BLK, INODE_ENTRY_TABLE_LENGTH, i_table) < 0)
  {
    printf("Error write INODE_ENTRY table to disk\n");
    return -1;
  }
  return 0;
}

int r_INODE_ENTRYTable(INODE_ENTRY *i_table)
{
  if (read_blocks(INODE_ENTRY_TABLE_BLK, INODE_ENTRY_TABLE_LENGTH, i_table) < 0)
  {
    printf("Error read INODE_ENTRY table from disk\n");
    return -1;
  }
  return 0;
}

int write_Directory(Directory *dir)
{ 
  if (write_blocks(Directory_BLK_INDEX, Directory_BLOCKS, dir) < 0)
  {
    printf("Error write Directory to disk\n");
    return -1;
  }
  return 0;
}

int read_Directory(Directory *dir)
{ 
  if (read_blocks(Directory_BLK_INDEX, Directory_BLOCKS, dir) < 0)
  {
    printf("Error read Directory from disk\n");
    return -1;
  }
  return 0;
}

int write_super_block(int *s_bx)
{
  if (write_blocks(0, 1, s_bx) < 0)
  {
    printf("Error write Directory to disk\n");
    return -1;
  }
  return 0;
}

int read_super_block(int *s_bx)
{
  if (read_blocks(0, 1, s_bx) < 0)
  {
    printf("Error read Directory from disk\n");
    return -1;
  }
  return 0;
}

void mksfs(int fresh)
{
  char *file_name = "cVirtualDisk.sfs";
  INT_SIZE = sizeof(int);

  if (fresh)
  {
    if (init_fresh_disk(file_name, BLOCKS_SIZE, NUM_OF_BLOCKS) < 0)
    {
      printf("can't Create Virtual Disk \n");
      return;
    }

    s_bx[0] = 0;
    s_bx[1] = BLOCKS_SIZE;
    s_bx[2] = NUM_OF_BLOCKS;
    s_bx[3] = INODE_ENTRY_TABLE_LENGTH;
    s_bx[4] = ROOT_DIRECT;
    if (write_super_block(s_bx) < 0)
    {
      printf("can't write super block to disk");
      return;
    }
    DI = 0;
    memset(dir, 0, Directory_LENGTH * sizeof(Directory));
    if (write_Directory(dir))
    {
      printf("can't write dir to disk\n");
      return;
    }
    memset(INODE_ENTRYTable, 0, NUM_INODE_ENTRYS * sizeof(INODE_ENTRY));

    INODE_ENTRY rootNode = {.mode = 1};
    INODE_ENTRYTable[ROOT_DIRECT] = rootNode;

    if (w_INODE_ENTRYTable(INODE_ENTRYTable))
    {
      printf("can't write INODE_ENTRY_table to disk\n");
      return;
    }

    memset(bitmap, 0xFF, 3125);
    bitmap[0] = 0;    
    bitmap[1] = 0;    
    bitmap[3124] = 0; 

    if (w_bit_map(bitmap))
    {
      printf("can't write bitmap to disk\n");
      return;
    }
  }
  else
  {
    
	
	
    if (init_disk(file_name, BLOCKS_SIZE, NUM_OF_BLOCKS))
    {
      printf("can't open V drive\n");
      return;
    }
   
   
   
   
    if (read_super_block(s_bx))
    {
      printf("can't read super block\n");
      return;
    }
    
	
	
	
    if (r_INODE_ENTRYTable(INODE_ENTRYTable))
    {
      printf("can't read INODE_ENTRY table\n");
      return;
    }
    
	
	
	
    if (read_Directory(dir))
    {
      printf("can't read Directory\n");
      return;
    }



    if (r_bit_map(bitmap))
    {
      printf("can't read bitmap");
      return;
    }
  }
}
int sfs_is_opened(int file){
  for (int i = 0; i < FILEDISCRIPTOR_TABLE_LENGTH; i++)
  {
  
    if (fileDiscripto[i].INODE_ENTRY == file)
    {
      return i;
    }
  }
 
  return -1;
}
int sfs_fopen(char *file_name)
{
  int fileDiscriptoIndex;
  if ((fileDiscriptoIndex = nextEmptyfileDiscripto()) < 0)
  {
  
    return -1;
  }
  int dirIndex = DirectoryFinder(file_name);
  int fileIndex;
  if (dirIndex == -1)
  {
    if ((fileIndex = sfs_fcreate(file_name)) < 0)
    {
      printf("can't create the file! \n");
      return -1;
    }
   
  }
  else
  {

    fileIndex = dir[dirIndex].INODE_ENTRY;
    
  }
  int fileOpened;
 if((fileOpened = sfs_is_opened(fileIndex)) > 0){
   return fileOpened;
 }
  file_Discriptor_Entry filedis = {.INODE_ENTRY = fileIndex, .read_pointer = 0, .write_pointer = INODE_ENTRYTable[fileIndex].size};
  fileDiscripto[fileDiscriptoIndex] = filedis;

 
  return fileDiscriptoIndex;
}
int sfs_fclose(int file)
{
  if (file < 0)
  {
    return -1;
  }
  if (!(fileDiscripto[file].INODE_ENTRY > 0 && fileDiscripto[file].INODE_ENTRY < NUM_INODE_ENTRYS))
  {
    return -1;
  }

  file_Discriptor_Entry empty = {0};
  fileDiscripto[file] = empty;
  return 0;
}

int sfs_frseek(int file, int pos)
{
  if (pos < 0 || pos > INODE_ENTRYTable[fileDiscripto[file].INODE_ENTRY].size)
  {
    printf(" Invalid reader position \n");
    return -1;
  }
  if (file < 0)
  {
    printf("Invalid file");
    return -1;
  }

  fileDiscripto[file].read_pointer = pos;
  return 0;
}

int sfs_fwseek(int file, int pos)
{
  if (pos < 0 || pos > INODE_ENTRYTable[fileDiscripto[file].INODE_ENTRY].size)
  {
    printf(" Invalid writer position \n");
    return -1;
  }
  if (file < 0)
  {
    printf("Invalid file");
    return -1;
  }
  if (!(fileDiscripto[file].INODE_ENTRY > 0 && fileDiscripto[file].INODE_ENTRY < NUM_INODE_ENTRYS))
  {
    return -1;
  }
  fileDiscripto[file].write_pointer = pos;
  return 0;
}

int sfs_remove(char *file_name)
{
  int file = DirectoryFinder(file_name);
  int INODE_ENTRYNum = dir[file].INODE_ENTRY;

  int file_dis;
  if ((file_dis = file_Discriptor_EntryFinder(INODE_ENTRYNum)) >= 0)
  {
    printf("closing file");
    if (sfs_fclose(file_dis))
    {
      printf("can't close file, aborting remove");
    }
  }
  memset(&dir[file], 0, sizeof(Directory));
  INODE_ENTRY *INODE_ENTRY = &INODE_ENTRYTable[INODE_ENTRYNum];

  for (int i = 0; i < 12; i++)
  {
    int pointer;
    if ((pointer = INODE_ENTRY->pointer[i]) > 0)
    {
      if (free_blocks(pointer) < 0)
      {
        printf("can't free blocks to remove files");
        return -1;
      }
    }
  }
  int pointerIndex = INODE_ENTRY->indirect_pointer;

  if (pointerIndex > 0)
  {
    int pointerBlock[1024 / INT_SIZE];
    if (read_blocks(pointerIndex, 1, pointerBlock) < 0)
    {
      printf(" can't read indirect pointer");
      return -1;
    }

    for (int i = 0; i < 1024 / INT_SIZE; i++)
    {
      int val;
      if ((val = pointerBlock[i]) > 0)
      {
        if (free_blocks(val) < 0)
        {
          printf(" failed to free indirect pointer");
          return -1;
        }
      }
    }
  }
  memset(&INODE_ENTRYTable[INODE_ENTRYNum], 0, sizeof(INODE_ENTRY));
  return 0;
}

int sfs_fread(int file, char *buffer, int length)
{
  if (length <= 0)
  {
    printf("invalid length\n");
    return -1;
  }
  if (file < 0)
  {
    return -1;
  }
  if (!(fileDiscripto[file].INODE_ENTRY > 0 && fileDiscripto[file].INODE_ENTRY < NUM_INODE_ENTRYS))
  {
    return -1;
  }

  int bytesR = 0;
  int indexR;

  char readBuff[length];

  memset(readBuff, 0, length * sizeof(char));
  INODE_ENTRY fileINODE_ENTRY = INODE_ENTRYTable[fileDiscripto[file].INODE_ENTRY];

  if (fileINODE_ENTRY.size == 0)
  {
    printf("file is empty");
  }

  int read_position = fileDiscripto[file].read_pointer / 1024;
  int read_offset = fileDiscripto[file].read_pointer % 1024;
  int lastR;
  if ((lastR = getBlocks(read_position, fileINODE_ENTRY)) < 0)
  {
    printf("can't get the block requested");
  }

  char block[1024];
  if (read_blocks(lastR, 1, block) < 0)
  {
    printf("can't read data pointed by write pointer");
    return -1;
  }

  indexR = read_offset;

  for (int i = 0; i < length; i++)
  {
    if (fileDiscripto[file].read_pointer > fileINODE_ENTRY.size)
    {
      memcpy(buffer, readBuff, length);
      return bytesR - 1;
    }
    if (indexR >= 1024)
    {
      memset(block, 0, 1024);
      read_position++;
      if ((lastR = getBlocks(read_position, fileINODE_ENTRY)) < 0)
      {
        printf("can't get block number ");
      }

      if (read_blocks(lastR, 1, block) < 0)
      {
        printf(" cound not read date block! \n");
        return -1;
      }
      indexR = 0;
    }

    readBuff[i] = block[indexR++];
    bytesR++;
    fileDiscripto[file].read_pointer++;
  }

  memcpy(buffer, readBuff, length);
  return bytesR;
}

int sfs_fwrite(int file, char *buf, int length)
{
  if (file < 0 || length < 0)
  {
    return -1;
  }
  if (fileDiscripto[file].INODE_ENTRY < 1)
  {
    printf("can't find file in File descriptor table");
    return -1;
  }
  INODE_ENTRY *fileINODE_ENTRY = &INODE_ENTRYTable[fileDiscripto[file].INODE_ENTRY];
  int Current_size = fileINODE_ENTRY->size;
  int currwrite_pointer = fileDiscripto[file].write_pointer;




  int bytesW = 0;
  int indexW;
  int CurrentBlock_number;

  int write_position = fileDiscripto[file].write_pointer / 1024;
  int write_offset = fileDiscripto[file].write_pointer % 1024;
  int lastW;

  if ((lastW = getBlocks(write_position, *fileINODE_ENTRY)) < 0)
  {
    printf(" cound not get block from read pointer");
  }

  int newBlock;
  if (lastW <= 0)
  {
    if((newBlock = nextfree_block()) < 1){
      printf("can't find new block"); 
      return bytesW *(-1);
    }
    if (addINODE_ENTRYBlock(newBlock, fileINODE_ENTRY) < 0)
    {
      printf("can't add new block to INODE_ENTRY table");
      return bytesW * (-1);
    }

    lastW = newBlock;
  }

  char rbuf[1024];

  if (read_blocks(lastW, 1, rbuf) < 0)
  {
    printf("can't read date block pointed to by write pointer");
    return -1;
  }

  indexW = write_offset;
  CurrentBlock_number = lastW;



  for (int i = 0; i < length; i++)
  {
    if (indexW >= 1024)
    {
      if (write_blocks(CurrentBlock_number, 1, rbuf) < 0)
      {
        printf("can't write to indirect pointer");
        return bytesW * (-1);
      }



      indexW = 0;
      memset(rbuf, 0, 1024);

      if ((CurrentBlock_number = getBlocks(++write_position, *fileINODE_ENTRY)) <= 0)
      {
        if ((newBlock = nextfree_block()) < 1)
        {
          printf("can't find new block");
          return bytesW * (-1);
        }
        if (addINODE_ENTRYBlock(newBlock, fileINODE_ENTRY) < 0)
        {
          return bytesW * (-1);
        }
        CurrentBlock_number = newBlock;
      }
      else
      {
        if (read_blocks(CurrentBlock_number, 1, rbuf) < 0)
        {
          printf("can't read data pointed to by the write pointer");
          return -1;
        }
        if (rbuf[1] == '\0')
        {
          printf(" loaded empty block");
        }
      }
    }
    rbuf[indexW++] = buf[i];
    bytesW++;
    fileDiscripto[file].write_pointer++;
    if (fileDiscripto[file].write_pointer > fileINODE_ENTRY->size)
    {
      fileINODE_ENTRY->size++;
    }
  }
  if ((write_blocks(CurrentBlock_number,1,rbuf))< 0){
    printf("can't write indirect pointer to block");
    return -1; 
  }

  if (sizeof(INODE_ENTRYTable)/1024 > INODE_ENTRY_TABLE_LENGTH){
    printf("INODE_ENTRY table is more than 20 blocks");
    return -1;
  }

  if ((write_blocks(INODE_ENTRY_TABLE_BLK, INODE_ENTRY_TABLE_BLK, INODE_ENTRYTable))< 0){
    printf("can't write INODE_ENTRY table to block"); 
    return -1;
  }

  if(fileDiscripto[file].write_pointer > Current_size){
    int alph = fileINODE_ENTRY -> size - Current_size;
    int extraLen = length -(Current_size - currwrite_pointer);
    if(alph != extraLen || fileDiscripto[file].write_pointer != (currwrite_pointer+length)){
      printf("length difference"); 

    }
  }
  return bytesW;
}