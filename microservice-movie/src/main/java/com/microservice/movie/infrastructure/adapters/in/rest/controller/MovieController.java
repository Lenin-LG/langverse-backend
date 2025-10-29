package com.microservice.movie.infrastructure.adapters.in.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservice.movie.application.ports.in.MovieServicePort;
import com.microservice.movie.application.ports.in.UploadMoviePort;
import com.microservice.movie.application.ports.out.FileStoragePort;
import com.microservice.movie.domain.model.Movie;
import com.microservice.movie.infrastructure.adapters.in.rest.dto.MovieAssetsDto;
import com.microservice.movie.infrastructure.adapters.in.rest.dto.MovieDto;
import com.microservice.movie.infrastructure.adapters.in.rest.exception.NoResultsFoundException;
import com.microservice.movie.infrastructure.adapters.in.rest.exception.ResourceNotFoundException;
import com.microservice.movie.infrastructure.adapters.in.rest.mapper.MovieRestMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MovieController {

    @Autowired
    private  MovieServicePort movieServicePort;
    @Autowired
    private  MovieRestMapper movieRestMapper;
    @Autowired
    private UploadMoviePort uploadMoviePort;
    @Autowired
    private FileStoragePort fileStoragePort;
    @Value("${aws.bucket}")
    private String bucket;
    /*
    * USER LAYER
    *
    * */
    // Get movie or episode data by ID
    @Operation(
            summary = "Get movie by ID",
            description = "Returns the data of a movie given its unique identifier",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Movie or episode found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MovieDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The movie or episode with the specified ID was not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"No movie was found with the specified ID\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error getting the movie\" }")
                            )
                    )
            }

    )

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        Movie movie = movieServicePort.findById(id);

        if (movie == null) {
            throw new ResourceNotFoundException("No movie was found with the specified ID");
        }

        MovieDto dto = mapToDtoWithSignedBanner(movie);
        return ResponseEntity.ok(dto);
    }
    //Search for movies or series activated for users by search bar
    @Operation(
            summary = "Search for movies or series",
            description = "Allows you to search for user-activated movies or series by title. Searches are performed using the search bar.",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "title",
                            description = "Text or keyword of the movie/series title to search for",
                            required = true,
                            example = "Inception"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Movies or series found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MovieDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or empty 'title' parameter",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"The 'title' parameter cannot be empty\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No results found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"No movies or series were found with the title: Inception\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error when searching for movies or series\" }")
                            )
                    )
            }
    )
    @GetMapping("/search-all")
    public ResponseEntity<?> searchByTitle(@RequestParam("title") String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("The 'title' parameter cannot be empty");
        }

        List<Movie> results = movieServicePort.searchByTitle(title);

        if (results.isEmpty()) {
            throw new NoResultsFoundException("No movies or series were found with the title: " + title);
        }

        List<MovieDto> dtos = results.stream()
                .map(this::mapToDtoWithSignedBanner)
                .toList();

        return ResponseEntity.ok(dtos);
    }
    //Generate playback URL for movie or episode
    @Operation(
            summary = "Generate playback URL",
            description = "Generates a temporary (pre-signed) URL to play a movie or episode in the specified quality. The URL automatically expires after 60 minutes.",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique ID of the movie or episode",
                            required = true,
                            example = "123"
                    ),
                    @Parameter(
                            name = "quality",
                            description = "Requested video quality",
                            required = true,
                            example = "720p",
                            schema = @Schema(allowableValues = {"480p", "720p", "1080p"})
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Playback URL generated successfully",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "https://s3.amazonaws.com/bucket/video-720p.mp4?X-Amz-Signature=abc123...")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid quality",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Invalid quality. Allowed options are 480p, 720p o 1080p.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie or episode not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Movie or episode not found with ID 123\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al generar la URL",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error generating playback URL\" }")
                            )
                    )
            }
    )
    @GetMapping("/play/{id}/{quality}")
    public ResponseEntity<String> getPlaybackUrl(@PathVariable("id") Long id, @PathVariable("quality") String quality) {
        Movie movie = movieServicePort.findById(id);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie or episode not found with ID " + id);
        }

        String key = switch (quality) {
            case "480p"  -> movie.getVideoUrl480p();
            case "720p"  -> movie.getVideoUrl720p();
            case "1080p" -> movie.getVideoUrl1080p();
            default      -> throw new IllegalArgumentException("Invalid quality. Allowed options are 480p, 720p o 1080p.");
        };

        String url = fileStoragePort.generatePresignedDownloadUrl(bucket, key, Duration.ofMinutes(60));
        return ResponseEntity.ok(url);
    }
    // List only movies or only series
    @Operation(
            summary = "List only movies or only series",
            description = "Returns a list of movies or series activated according to the specified category. " +
                    "If the category is 2, series are considered; otherwise, movies are returned.",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "categoryId",
                            description = "Category identifier (1 = movie, 2 = Series)",
                            required = true,
                            example = "2"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of movies or series obtained correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MovieDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameter",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"The categoryId parameter must be 1 (movie) o 2 (Series).\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No results found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"No results found for the category 2.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error when getting the list of movies or series.\" }")
                            )
                    )
            }
    )
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<?> getByCategory(@PathVariable("categoryId")  Long categoryId) {
        // Validate the parameter
        if (categoryId < 1 || categoryId > 2) {
            throw new IllegalArgumentException("The categoryId parameter must be 1 (Movies) or 2 (Series).");
        }

        List<Movie> baseList = movieServicePort.findAll().stream()
                .filter(movie -> movie.getIdCategory().equals(categoryId))
                .filter(movie -> Boolean.TRUE.equals(movie.getEstate()))
                .toList();

        if (baseList.isEmpty()) {
            throw new NoResultsFoundException("No results found for the category " + categoryId + ".");
        }

        List<Movie> filteredMovies;

        if (categoryId == 2) { // Series
            filteredMovies = baseList.stream()
                    .collect(Collectors.groupingBy(movie -> normalizeTitle(movie.getTitle())))
                    .values().stream()
                    .map(group -> group.stream()
                            .filter(e -> e.getSeasonNumber() != null)
                            .min(Comparator.comparingInt(Movie::getSeasonNumber)
                                    .thenComparingInt(Movie::getEpisodeNumber))
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
        } else { // Movies
            filteredMovies = baseList;
        }

        List<MovieDto> result = filteredMovies.stream()
                .map(this::mapToDtoWithSignedBanner)
                .toList();

        return ResponseEntity.ok(result);
    }
    //list episodes of a series-- Do not use it as a user part
    @Hidden
    @GetMapping("/series/{title}/season/{seasonNumber}/episodes")
    public ResponseEntity<List<MovieDto>> getEpisodesBySeriesTitleAndSeason(
            @PathVariable("title") String title,
            @PathVariable("seasonNumber") Integer seasonNumber) {

        List<Movie> episodes = movieServicePort.getEpisodesBySeriesTitle(title).stream()
                .filter(m -> Objects.equals(m.getSeasonNumber(), seasonNumber))
                .sorted(Comparator.comparing(Movie::getEpisodeNumber)) // sort
                .toList();
        Duration duration = Duration.ofMinutes(60);

        List<MovieDto> dtoList = new ArrayList<>();

        for (int i = 0; i < episodes.size(); i++) {
            Movie movie = episodes.get(i);
            MovieDto dto = movieRestMapper.toDto(movie);

            // Only the first episode loads the banner with presigned URL
            if (i == 0 && movie.getImageBanner() != null) {
                String key = movie.getImageBanner();
                String bannerUrl = fileStoragePort.generatePresignedDownloadUrl(bucket, key, duration);
                dto.setImageBanner(bannerUrl);
            }

            dtoList.add(dto);
        }

        return ResponseEntity.ok(dtoList);
    }
    //Get urls
    @Operation(
            summary = "Get URLs of assets from a movie or series",
            description = "Generates temporary (pre-signed) URLs valid for 60 minutes to access a movie or series' multimedia resources: audio, subtitles, and banner images.",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique ID of the movie or series",
                            required = true,
                            example = "123"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Assets generated correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MovieAssetsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The movie or series with the specified ID was not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Movie or series not found with ID 123.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error while generating assets",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error while generating assets.\" }")
                            )
                    )
            }
    )
    @GetMapping("/{id}/assets")
    public ResponseEntity<?> getMovieAssets(@PathVariable("id") Long id) {
        Movie movie = movieServicePort.findById(id);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie or series not found with ID " + id + ".");
        }

        Duration duration = Duration.ofMinutes(60);
        MovieAssetsDto assets = new MovieAssetsDto();
        assets.setAudioEsUrl(fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getAudioUrlEs(), duration));
        assets.setAudioEnUrl(fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getAudioUrlEn(), duration));
        assets.setSubtitlesEsUrl(fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getSubTitlesSpanish(), duration));
        assets.setSubtitlesEnUrl(fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getSubTitlesEnglish(), duration));
        assets.setImageBannerUrl(fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getImageBanner(), duration));

        return ResponseEntity.ok(assets);
    }
    //list all episodes of x series to list
    @Operation(
            summary = "List all episodes of a series grouped by season",
            description = "Retrieve all episodes of a given series, grouped by season number. "
                    + "Each episode includes its signed image banner URL.",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Episodes successfully retrieved and grouped by season"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No episodes found for the specified series",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"No episodes were found for the indicated series.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error while processing the request.\" }")
                            )
                    )
            }
    )
    @GetMapping("/series/{title}/episodes-all")
    public ResponseEntity<?> getAllEpisodesGroupedBySeason(@PathVariable("title")  String title) {
        List<Movie> episodes = movieServicePort.getEpisodesBySeriesTitle(title);

        if (episodes == null || episodes.isEmpty()) {
            throw new NoResultsFoundException("No episodes were found for the indicated series.");
        }

        Duration duration = Duration.ofMinutes(60);

        // Group by season number and map DATA to signed URLs
        Map<Integer, List<MovieDto>> grouped = episodes.stream()
                .collect(Collectors.groupingBy(
                        Movie::getSeasonNumber,
                        TreeMap::new,
                        Collectors.mapping(
                                movie -> {
                                    MovieDto dto = movieRestMapper.toDto(movie);
                                    dto.setImageBanner(fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getImageBanner(), duration));
                                    return dto;
                                },
                                Collectors.toList()
                        )
                ));

        return ResponseEntity.ok(grouped);
    }
    /*
     * CAPA ADMINISTRATOR
     *
     *
     * */

    //Upload movie or episode (with video, audio, subtitles and image)
    @Operation(
            summary = "Upload a new movie or episode with media files",
            description = "Allows an admin to upload a movie or episode with its associated media files asynchronously. Returns immediately the movie metadata with status 202.",
            tags = {"Admin"},
            responses = {
                    @ApiResponse(responseCode = "202", description = "Movie/episode upload accepted for processing",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MovieDto.class),
                                    examples = @ExampleObject(
                                            name = "movieExample",
                                            value = """
                    {
                      "idMovie": 1,
                      "title": "Spy x family",
                      "description": "Una serie muy buena",
                      "idCategory": 2,
                      "videoUrl480p": null,
                      "videoUrl720p": null,
                      "videoUrl1080p": null,
                      "audioUrlEn": null,
                      "audioUrlEs": null,
                      "subTitlesEnglish": null,
                      "subTitlesSpanish": null,
                      "durationInMinutes": 45,
                      "releaseDate": "2023-07-01",
                      "seasonNumber": 1,
                      "episodeNumber": 1,
                      "imageBanner": null,
                      "estate": null
                    }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input data or missing files",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Invalid input data or missing files")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - admin role required",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Unauthorized - admin role required")
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Forbidden - insufficient permissions")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error while handling the upload",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Internal server error while handling the upload")
                            )
                    )
            }
    )
    @PostMapping(value = "/create/s3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<MovieDto> createMovieWithUpload(
            @RequestPart("movie") String movieJson,
            @RequestPart("file") MultipartFile file,
            @RequestPart("audioEn") MultipartFile audioEn,
            @RequestPart("audioEs") MultipartFile audioEs,
            @RequestPart("subsEn") MultipartFile subsEn,
            @RequestPart("subsEs") MultipartFile subsEs,
            @RequestPart("imageBanner") MultipartFile image) throws Exception {
            if (file.isEmpty() || audioEn.isEmpty() || audioEs.isEmpty() || subsEn.isEmpty() || subsEs.isEmpty() || image.isEmpty()) {
            throw new IllegalArgumentException("All media files are required");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            MovieDto movieDto = objectMapper.readValue(movieJson, MovieDto.class);
            Movie movie = movieRestMapper.toDomain(movieDto);


            Movie saved = movieServicePort.save(movie);


            Path tempDir = Files.createTempDirectory("upload_temp_" + UUID.randomUUID());

            Path videoPath = tempDir.resolve("video.mp4");
            file.transferTo(videoPath.toFile());

            Path audioEnPath = tempDir.resolve("audio_en.m4a");
            audioEn.transferTo(audioEnPath.toFile());

            Path audioEsPath = tempDir.resolve("audio_es.m4a");
            audioEs.transferTo(audioEsPath.toFile());

            Path subsEnPath = tempDir.resolve("subs_en.vtt");
            subsEn.transferTo(subsEnPath.toFile());

            Path subsEsPath = tempDir.resolve("subs_es.vtt");
            subsEs.transferTo(subsEsPath.toFile());

            Path imagePath = tempDir.resolve("banner.jpg");
            image.transferTo(imagePath.toFile());

            //We call the asynchronous method passing the paths
            uploadMoviePort.processAndUploadMovieAsync(
                    videoPath, audioEnPath, audioEsPath, subsEnPath, subsEsPath, imagePath, saved
            );

            //We return success without waiting for processing
            MovieDto responseDto = movieRestMapper.toDto(saved);
            return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);

    }
    //Update movie/episode
    @Operation(
            summary = "Update movie or episode",
            description = "Allows an admin to update the metadata of an existing movie or episode "
                    + "by providing its ID and the updated details in the request body.",
            tags = {"Admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Movie or episode updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MovieDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid data or validation error in the request body",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"The title cannot be empty.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — administrator role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Access denied: administrator role required\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The movie or episode with the specified ID was not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Movie or episode not found.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error while updating the movie or episode.\" }")
                            )
                    )
            }
    )
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid MovieDto movieDto) {
        // We convert the DTO to a domain
        Movie movie = movieRestMapper.toDomain(movieDto);

        // We try to update
        Movie updated = movieServicePort.updateById(id, movie);

        if (updated == null) {
            // We throw an exception that GlobalExceptionHandler will handle as 404
            throw new ResourceNotFoundException("Película o episodio no encontrado.");
        }

        // We return success
        return ResponseEntity.ok(movieRestMapper.toDto(updated));
    }
    //List all
    @Operation(
            summary = "List all movies/episodes",
            description = "Retrieve a complete list of all movies and episodes available in the system. "
                    + "Accessible only by admins.",
            tags = {"Admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Movies and episodes successfully recovered",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MovieDto.class)),
                                    examples = @ExampleObject(
                                            name = "Example list",
                                            value = "[{\n" +
                                                    "  \"id\": 1,\n" +
                                                    "  \"title\": \"Inception\",\n" +
                                                    "  \"duration\": 148,\n" +
                                                    "  \"idCategory\": 1\n" +
                                                    "}, {\n" +
                                                    "  \"id\": 2,\n" +
                                                    "  \"title\": \"Breaking Bad - S01E01\",\n" +
                                                    "  \"seasonNumber\": 1,\n" +
                                                    "  \"episodeNumber\": 1,\n" +
                                                    "  \"idCategory\": 2\n" +
                                                    "}]"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — administrator role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Access denied: Administrator role required.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al recuperar los datos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error while getting list of movies or episodes.\" }")
                            )
                    )
            }
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> getAllMovies() {
        List<MovieDto> movies = movieServicePort.findAll()
                .stream()
                .map(movieRestMapper::toDto)
                .toList();

        return ResponseEntity.ok(movies);
    }
    //Turn a movie or episode on or off
    @Operation(
            summary = "Enable or disable a movie/episode",
            description = "Allows an admin to turn a movie or episode ON or OFF by its ID. "
                    + "The `active` parameter determines the new status.",
            tags = {"Admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"message\": \"The movie was activated successfully.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetro inválido o solicitud incorrecta",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"The 'active' parameter must be true or false.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — administrator role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Access denied: Administrator role required.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie or episode not found with the specified ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"The movie or episode with the specified ID was not found.\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error while updating status",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Unexpected error while updating movie or episode status.\" }")
                            )
                    )
            }
    )
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> toggleStatus(@PathVariable("id")  Long id, @RequestParam("active") boolean active) {
        movieServicePort.setActiveStatus(id, active);

        String message = active
                ? "The movie or episode was activated successfully."
                : "The movie or episode was successfully disabled.";

        return ResponseEntity.ok(Map.of("message", message));
    }

    private MovieDto mapToDtoWithSignedBanner(Movie movie) {
        MovieDto dto = movieRestMapper.toDto(movie);

        Duration duration = Duration.ofMinutes(60);

        if (movie.getImageBanner() != null && !movie.getImageBanner().isBlank()) {
            String signedUrl = fileStoragePort.generatePresignedDownloadUrl(bucket, movie.getImageBanner(), duration);
            dto.setImageBanner(signedUrl);
        } else {
            dto.setImageBanner(null);
        }
        return dto;
    }
    private String normalizeTitle(String title) {
        return title
                .replaceAll("(?i)season\\s*\\d+", "")     // Delete 'season X'
                .replaceAll("\\s+", "")                  // Delete all  spaces
                .toLowerCase();
    }

}
