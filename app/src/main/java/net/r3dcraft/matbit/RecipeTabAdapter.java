package net.r3dcraft.matbit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * The RecipeTabAdapter class extends FragmentPagerAdapter and initializes and loads all the
 * fragments in the RecipeActivity in order, with a tab layout. It also sets the correct titles to
 * each fragment.
 */

public class RecipeTabAdapter extends FragmentPagerAdapter {

    private static final String FRAGMENT_RECIPE_INFO_TITLE = "Info";
    private static final String FRAGMENT_RECIPE_INGREDIENTS_TITLE = "Ingredienser";
    private static final String FRAGMENT_RECIPE_STEPS_TITLE = "Steg";
    private static final String FRAGMENT_RECIPE_COMMENTS_TITLE = "Kommentarer";
    private RecipeFragmentInfo fragmentRecipeInfo;
    private RecipeFragmentIngredients fragmentRecipeIngredients;
    private RecipeFragmentSteps fragmentRecipeSteps;
    private RecipeFragmentComments fragmentRecipeComments;

    /**
     * RecipeTabAdapter Constructor
     * @param fm
     * @param bundle Bundle from RecipeActivity with recipe ID/KEY
     */
    public RecipeTabAdapter(FragmentManager fm,  Bundle bundle) {
        super(fm);
        this.fragmentRecipeInfo = new RecipeFragmentInfo();
        this.fragmentRecipeIngredients = new RecipeFragmentIngredients();
        this.fragmentRecipeSteps = new RecipeFragmentSteps();
        this.fragmentRecipeComments = new RecipeFragmentComments();
        this.fragmentRecipeInfo.setArguments(bundle);
        this.fragmentRecipeIngredients.setArguments(bundle);
        this.fragmentRecipeSteps.setArguments(bundle);
        this.fragmentRecipeComments.setArguments(bundle);
    }

    /**
     * Get fragment at position.
     * @param position position of requested fragment
     * @return requested fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragmentRecipeInfo;
            case 1:
                return fragmentRecipeIngredients;
            case 2:
                return fragmentRecipeSteps;
            case 3:
                return fragmentRecipeComments;
            default:
                break;
        }
        return null;
    }

    /**
     * Get the amount of fragments is this adapter
     * @return amount of fragments
     */
    @Override
    public int getCount() {
        return 4;
    }

    /**
     * Get the title of a specific fragment.
     * @param position position of fragment
     * @return title of fragment
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return FRAGMENT_RECIPE_INFO_TITLE;
            case 1:
                return FRAGMENT_RECIPE_INGREDIENTS_TITLE;
            case 2:
                return FRAGMENT_RECIPE_STEPS_TITLE;
            case 3:
                return FRAGMENT_RECIPE_COMMENTS_TITLE;
            default:
                break;
        }
        return null;
    }
}
