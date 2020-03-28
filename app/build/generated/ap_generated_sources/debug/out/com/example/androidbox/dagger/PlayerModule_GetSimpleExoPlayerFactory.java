// Generated by Dagger (https://google.github.io/dagger).
package com.example.androidbox.dagger;

import android.content.Context;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class PlayerModule_GetSimpleExoPlayerFactory implements Factory<SimpleExoPlayer> {
  private final PlayerModule module;

  private final Provider<Context> contextProvider;

  private final Provider<DefaultTrackSelector> trackSelectorProvider;

  public PlayerModule_GetSimpleExoPlayerFactory(
      PlayerModule module,
      Provider<Context> contextProvider,
      Provider<DefaultTrackSelector> trackSelectorProvider) {
    this.module = module;
    this.contextProvider = contextProvider;
    this.trackSelectorProvider = trackSelectorProvider;
  }

  @Override
  public SimpleExoPlayer get() {
    return provideInstance(module, contextProvider, trackSelectorProvider);
  }

  public static SimpleExoPlayer provideInstance(
      PlayerModule module,
      Provider<Context> contextProvider,
      Provider<DefaultTrackSelector> trackSelectorProvider) {
    return proxyGetSimpleExoPlayer(module, contextProvider.get(), trackSelectorProvider.get());
  }

  public static PlayerModule_GetSimpleExoPlayerFactory create(
      PlayerModule module,
      Provider<Context> contextProvider,
      Provider<DefaultTrackSelector> trackSelectorProvider) {
    return new PlayerModule_GetSimpleExoPlayerFactory(
        module, contextProvider, trackSelectorProvider);
  }

  public static SimpleExoPlayer proxyGetSimpleExoPlayer(
      PlayerModule instance, Context context, DefaultTrackSelector trackSelector) {
    return Preconditions.checkNotNull(
        instance.getSimpleExoPlayer(context, trackSelector),
        "Cannot return null from a non-@Nullable @Provides method");
  }
}
