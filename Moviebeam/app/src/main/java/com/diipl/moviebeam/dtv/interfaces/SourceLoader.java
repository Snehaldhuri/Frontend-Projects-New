package com.diipl.moviebeam.dtv.interfaces;

import android.content.Context;

import java.io.InputStream;

import kotlin.Metadata;

public interface SourceLoader {
    InputStream loadFrequencySource(Context context);

    InputStream loadSatelliteSource(Context context);
}
