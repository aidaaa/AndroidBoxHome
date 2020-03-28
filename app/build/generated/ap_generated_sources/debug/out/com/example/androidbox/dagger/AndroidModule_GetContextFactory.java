// Generated by Dagger (https://google.github.io/dagger).
package com.example.androidbox.dagger;

import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class AndroidModule_GetContextFactory implements Factory<Context> {
  private final AndroidModule module;

  public AndroidModule_GetContextFactory(AndroidModule module) {
    this.module = module;
  }

  @Override
  public Context get() {
    return provideInstance(module);
  }

  public static Context provideInstance(AndroidModule module) {
    return proxyGetContext(module);
  }

  public static AndroidModule_GetContextFactory create(AndroidModule module) {
    return new AndroidModule_GetContextFactory(module);
  }

  public static Context proxyGetContext(AndroidModule instance) {
    return Preconditions.checkNotNull(
        instance.getContext(), "Cannot return null from a non-@Nullable @Provides method");
  }
}
