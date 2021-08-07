// IBinderPool.aidl
package com.sunnyday.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
 /**
  * @param binderType: the unique token of specific Binder
  * @return specific Binder who's token is binderType
  */
  IBinder getBinderByType(int binderType);
}