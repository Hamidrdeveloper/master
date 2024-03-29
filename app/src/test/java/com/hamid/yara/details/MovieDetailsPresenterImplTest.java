package com.hamid.yara.details;

import com.hamid.yara.Movie;
import com.hamid.yara.Review;
import com.hamid.yara.RxSchedulerRule;
import com.hamid.yara.Video;
import com.hamid.yara.favorites.FavoritesInteractor;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author arunsasidharan
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieDetailsPresenterImplTest {
    @Rule
    public RxSchedulerRule rule = new RxSchedulerRule();
    @Mock
    private MovieDetailsView view;
    @Mock
    private MovieDetailsInteractor movieDetailsInteractor;
    @Mock
    private FavoritesInteractor favoritesInteractor;
    @Mock
    List<Video> videos;
    @Mock
    Movie movie;
    @Mock
    List<Review> reviews;

    private MovieDetailsPresenterImpl movieDetailsPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        movieDetailsPresenter = new MovieDetailsPresenterImpl(movieDetailsInteractor, favoritesInteractor);
        movieDetailsPresenter.setView(view);
    }

    @After
    public void teardown() {
        movieDetailsPresenter.destroy();
    }

    @Test
    public void shouldUnfavoriteIfFavoriteTapped() {
        when(movie.getImdbID()).thenReturn("12345");
        when(favoritesInteractor.isFavorite(movie.getImdbID())).thenReturn(true);

        movieDetailsPresenter.onFavoriteClick(movie);

        verify(view).showUnFavorited();
    }

    @Test
    public void shouldFavoriteIfUnfavoriteTapped() {
        when(movie.getImdbID()).thenReturn("12345");
        when(favoritesInteractor.isFavorite(movie.getImdbID())).thenReturn(false);

        movieDetailsPresenter.onFavoriteClick(movie);

        verify(view).showFavorited();
    }

    @Test
    public void shouldBeAbleToShowTrailers() {
        when(movie.getImdbID()).thenReturn("12345");
        Observable<List<Video>> responseObservable = Observable.just(videos);
        when(movieDetailsInteractor.getTrailers(movie.getImdbID())).thenReturn(responseObservable);

        movieDetailsPresenter.showTrailers(movie);

        verify(view).showTrailers(videos);
    }

    @Test
    public void shouldFailSilentlyWhenNoTrailers() throws Exception {
        when(movie.getImdbID()).thenReturn("12345");
        when(movieDetailsInteractor.getTrailers(movie.getImdbID())).thenReturn(Observable.error(new SocketTimeoutException()));

        movieDetailsPresenter.showTrailers(movie);

        verifyZeroInteractions(view);
    }

    @Test
    public void shouldBeAbleToShowReviews() {
        Observable<List<Review>> responseObservable = Observable.just(reviews);
        when(movie.getImdbID()).thenReturn("12345");
        when(movieDetailsInteractor.getReviews(movie.getImdbID())).thenReturn(responseObservable);

        movieDetailsPresenter.showReviews(movie);

        verify(view).showReviews(reviews);
    }


    @Test
    public void shouldFailSilentlyWhenNoReviews() throws Exception {
        when(movie.getImdbID()).thenReturn("12345");
        when(movieDetailsInteractor.getReviews(movie.getImdbID())).thenReturn(Observable.error(new SocketTimeoutException()));

        movieDetailsPresenter.showReviews(movie);

        verifyZeroInteractions(view);
    }

}