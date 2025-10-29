package com.microservice.movie.application.ports.in;

import com.microservice.movie.domain.model.Movie;
import java.nio.file.Path;

public interface UploadMoviePort {
    void  processAndUploadMovieAsync(  Path videoPath,
                                       Path audioEnPath,
                                       Path audioEsPath,
                                       Path subsEnPath,
                                       Path subsEsPath,
                                       Path imagePath,
                                       Movie movie);
}