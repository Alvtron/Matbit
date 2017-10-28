package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public final class RecipeExample {

    public static Recipe recipeExample1 () {
        RecipeData recipeData = new RecipeData();
        recipeData.setTitle("Fiskeburger med coleslaw");
        recipeData.setUser("k7wRLHlSaRMbHU34LFJw67mqNy82");
        recipeData.setDatetime_created("2015-07-11 15:16:17");
        recipeData.setDatetime_updated(DateTime.nowString());
        recipeData.setTime(30);
        recipeData.setPortions(4);
        recipeData.setViews(224);
        recipeData.addRating(new Rating("testuser2", true, DateTime.nowString()));
        recipeData.addRating(new Rating("testuser3", true, DateTime.nowString()));
        recipeData.addRating(new Rating("testuser4", false, DateTime.nowString()));
        recipeData.addRating(new Rating("testuser5", false, DateTime.nowString()));
        recipeData.addComments(new Comment("testuser2", "Nam!", DateTime.nowString()));
        recipeData.addComments(new Comment("testuser3", "Dette var ikke godt!!", DateTime.nowString()));
        recipeData.addComments(new Comment("testuser4", "Dette ver godt!", DateTime.nowString()));
        recipeData.addStep(new Step("Kutt kålhode, gulrot og vårløk i tynne strimler. Bland inn rømme, honning, grovkuttet basilikum, pinjekjerner. Smak til med salt og pepper."));
        recipeData.addStep(new Step("Stek fiskeburgere på begge sider i en middels varm stekepanne med margarin til de er gjennomvarme, ca. 6-8 minutter. Legg fiskeburgere på burgerbrød og topp med coleslaw."));
        recipeData.addIngredient(new Ingredient("Hoved", "Rød spisskål", 0.5, "stk"));
        recipeData.addIngredient(new Ingredient("Hoved", "Gulrot", 350, "g"));
        recipeData.addIngredient(new Ingredient("Hoved", "Vårløk", 0.5, "bunt"));
        recipeData.addIngredient(new Ingredient("Hoved", "Lettrømme", 0.5, "boks"));
        recipeData.addIngredient(new Ingredient("Hoved", "Honning", 1.0, "ss"));
        recipeData.addIngredient(new Ingredient("Hoved", "Basilikum", 0.5, "potte"));
        recipeData.addIngredient(new Ingredient("Hoved", "Pinjekjerner", 70, "g"));
        recipeData.addIngredient(new Ingredient("Hoved", "Salt", 0.5, "ts"));
        recipeData.addIngredient(new Ingredient("Hoved", "Pepper", 0.25, "ts"));
        Recipe recipe = new Recipe();
        recipe.setId("recipeid1");
        recipe.setData(recipeData);
        return recipe;
    }

    public static Recipe recipeExample2 () {
        RecipeData recipeData = new RecipeData();
        recipeData.setTitle("Fiskepudding med søtpotet og sukkererter");
        recipeData.setUser("k7wRLHlSaRMbHU34LFJw67mqNy82");
        recipeData.setDatetime_created("2016-07-11 15:16:17");
        recipeData.setDatetime_updated(DateTime.nowString());
        recipeData.setTime(30);
        recipeData.setPortions(2);
        recipeData.setViews(568);
        recipeData.addRating(new Rating("testuser1", true, DateTime.nowString()));
        recipeData.addRating(new Rating("testuser3", true, DateTime.nowString()));
        recipeData.addRating(new Rating("testuser4", true, DateTime.nowString()));
        recipeData.addRating(new Rating("testuser5", false, DateTime.nowString()));
        recipeData.addComments(new Comment("testuser1", "Perfekt!", DateTime.nowString()));
        recipeData.addComments(new Comment("testuser3", "Forferdelig!", DateTime.nowString()));
        recipeData.addComments(new Comment("testuser4", "Hei, mitt navn er Ola Hansen, og jeg har bare lyst til å fortelle om den gang...", DateTime.nowString()));
        recipeData.addStep(new Step("coursevarm stekeovnen til 220 °C. legg bakepapir på et stekebrett."));
        recipeData.addStep(new Step("Skjær fiskepudding i tykke skiver. Sett til side."));
        recipeData.addStep(new Step("Vask søtpotet grundig og skrap vekk eventuelle skader og urenheter i skallet. Del den i to og deretter i grove, tykke biter på langs. Legg bitene ut over bakepapiret og drypp over sitronsaft og olivenolje. Krydre med salt og pepper."));
        recipeData.addStep(new Step("Sett brettet nest øverst i ovnen og stek søtpoteten til den er myk og har fått god farge over det meste."));
        recipeData.addStep(new Step("Yoghurtdressing: Bland sammen alle ingrediensene til dressingen. Smak til med sitronsaft, salt og pepper."));
        recipeData.addStep(new Step("Kutt sukkerertene i fine strimler."));
        recipeData.addStep(new Step("Varm en stekepanne på middels varme og smelt margarin. Legg i fiskepuddingskivene og stek dem på begge sider til de har fått fin farge og er gjennomvarme."));
        recipeData.addStep(new Step("Server fiskepudding med søtpotet og yoghurtdressing og dryss finkuttede sukkererter over."));
        recipeData.addIngredient(new Ingredient("Hoved", "Fiskepudding", 500, "g"));
        recipeData.addIngredient(new Ingredient("Hoved", "Margarin", 2, "ss"));
        recipeData.addIngredient(new Ingredient("Hoved", "Søtpotet", 1, "stk"));
        recipeData.addIngredient(new Ingredient("Hoved", "Olivenolje", 2, "ss"));
        recipeData.addIngredient(new Ingredient("Hoved", "Salt", 0.5, "ts"));
        recipeData.addIngredient(new Ingredient("Hoved", "pepper", 0.25, "ts"));
        recipeData.addIngredient(new Ingredient("Hoved", "Sukkererter", 0.25, "poser"));
        recipeData.addIngredient(new Ingredient("Yoghurtdressing", "Matyoghurt", 1.5, "dl"));
        recipeData.addIngredient(new Ingredient("Yoghurtdressing", "Grov sennep", 1, "ss"));
        recipeData.addIngredient(new Ingredient("Yoghurtdressing", "Tørket timian", 1.5, "ts"));
        recipeData.addIngredient(new Ingredient("Yoghurtdressing", "Salt", 0.5, "ts"));
        recipeData.addIngredient(new Ingredient("Yoghurtdressing", "Pepper", 0.25, "ts"));
        Recipe recipe = new Recipe();
        recipe.setId("recipeid2");
        recipe.setData(recipeData);
        return recipe;
    }
}
