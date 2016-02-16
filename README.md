## PopularMovies 
### Project 1 & 2 of [Udacity Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree--nd801)
PopularMovies app allows you to load list of movies from [themoviedb.org](https://www.themoviedb.org/) sorted by popularity or rating. For each movie you can view description, ratings, posters, trailers and reviews.

### Install
```
$ git clone https://github.com/seliverstov/PopularMovies
$ cd PopularMovies
```
Go to `app/src/main/java/com/seliverstov/popularmovies/rest`, open `TMDBKey`class in text editor and put your api key for [themoviedb.org](https://www.themoviedb.org/) to field `API_KEY`. 

```
public class TMDBKey {
    public static final String API_KEY = "PUT_YOUR_API_KEY_HERE";
}
```
then return to project's root folder and run
```
$ gradle installDebug
```
###License

The contents of this repository are covered under the [MIT License](http://choosealicense.com/licenses/mit/).






