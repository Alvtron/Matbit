package net.r3dcraft.matbit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class RecipeTabAdapter extends FragmentPagerAdapter {

    private static final String FRAGMENT_RECIPE_INFO_TITLE = "Info";
    private static final String FRAGMENT_RECIPE_INGREDIENTS_TITLE = "Ingredienser";
    private static final String FRAGMENT_RECIPE_STEPS_TITLE = "Steg";
    private static final String FRAGMENT_RECIPE_COMMENTS_TITLE = "Kommentarer";
    private FragmentRecipeInfo fragmentRecipeInfo;
    private FragmentRecipeIngredients fragmentRecipeIngredients;
    private FragmentRecipeSteps fragmentRecipeSteps;
    private FragmentRecipeComments fragmentRecipeComments;

    public RecipeTabAdapter(FragmentManager fm,  Bundle bundle) {
        super(fm);
        this.fragmentRecipeInfo = new FragmentRecipeInfo();
        this.fragmentRecipeIngredients = new FragmentRecipeIngredients();
        this.fragmentRecipeSteps = new FragmentRecipeSteps();
        this.fragmentRecipeComments = new FragmentRecipeComments();
        this.fragmentRecipeInfo.setArguments(bundle);
        this.fragmentRecipeIngredients.setArguments(bundle);
        this.fragmentRecipeSteps.setArguments(bundle);
        this.fragmentRecipeComments.setArguments(bundle);
    }

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

    @Override
    public int getCount() {
        return 4;
    }

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

    public FragmentRecipeInfo getFragmentRecipeInfo() {
        return fragmentRecipeInfo;
    }

    public void setFragmentRecipeInfo(FragmentRecipeInfo fragmentRecipeInfo) {
        this.fragmentRecipeInfo = fragmentRecipeInfo;
    }

    public FragmentRecipeIngredients getFragmentRecipeIngredients() {
        return fragmentRecipeIngredients;
    }

    public void setFragmentRecipeIngredients(FragmentRecipeIngredients fragmentRecipeIngredients) {
        this.fragmentRecipeIngredients = fragmentRecipeIngredients;
    }

    public FragmentRecipeSteps getFragmentRecipeSteps() {
        return fragmentRecipeSteps;
    }

    public void setFragmentRecipeSteps(FragmentRecipeSteps fragmentRecipeSteps) {
        this.fragmentRecipeSteps = fragmentRecipeSteps;
    }

    public FragmentRecipeComments getFragmentRecipeComments() {
        return fragmentRecipeComments;
    }

    public void setFragmentRecipeComments(FragmentRecipeComments fragmentRecipeComments) {
        this.fragmentRecipeComments = fragmentRecipeComments;
    }

    public void setInfoBundle(Bundle bundle) {
        fragmentRecipeInfo.setArguments(bundle);
    }

    public void setIngredientsBundle(Bundle bundle) {
        fragmentRecipeIngredients.setArguments(bundle);
    }

    public void setStepsBundle(Bundle bundle) {
        fragmentRecipeSteps.setArguments(bundle);
    }

    public void setCommentBundle(Bundle bundle) {
        fragmentRecipeComments.setArguments(bundle);
    }
}
