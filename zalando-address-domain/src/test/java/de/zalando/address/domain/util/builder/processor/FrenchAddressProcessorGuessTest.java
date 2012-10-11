package de.zalando.address.domain.util.builder.processor;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.address.domain.util.builder.AddressBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class FrenchAddressProcessorGuessTest {

    private final String city;

    private final String zip;

    private final String streetName;

    private final String expectedCity;

    private final String expectedZip;

    private final String expectedName;

    private final String expectedNr;

    private final String expectedAdditional;

    // @formatter: off
    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "4 A rue louis de xiv", "Paris", "12345", "Rue Louis De XIV", "4a",
                null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "3 rue de Touraine", "Paris", "12345", "Rue De Touraine", "3", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1 'RUE CHE GUEVARA'", "Paris", "12345", "Rue Che Guevara", "1", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 RUE DU LOT ET GARONNE", "Paris", "12345", "Rue Du Lot Et Garonne",
                "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Lot Coum du Haut", "Paris", "12345", "Lotissement Coum Du Haut",
                "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Lot. Coum du Haut", "Paris", "12345", "Lotissement Coum Du Haut",
                "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Lot   . Coum du Haut", "Paris", "12345",
                "Lotissement Coum Du Haut", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Logt Coum du Haut", "Paris", "12345", "Lotissement Coum Du Haut",
                "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Logt. Coum du Haut", "Paris", "12345", "Lotissement Coum Du Haut",
                "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Logt   . Coum du Haut", "Paris", "12345",
                "Lotissement Coum Du Haut", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 lotissmt Coum du Haut", "Paris", "12345",
                "Lotissement Coum Du Haut", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 lotissmt. Coum du Haut", "Paris", "12345",
                "Lotissement Coum Du Haut", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 lotissmt   . Coum du Haut", "Paris", "12345",
                "Lotissement Coum Du Haut", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 SAINT AVE D EN BAS", "Paris", "12345", "Saint Ave D En Bas", "1",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 LIEU DIT SAINT AVE HUELLA", "Paris", "12345",
                "Lieu Dit Saint Ave Huella", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 RUE DE L AVE MARIA", "Paris", "12345", "Rue De L Ave Maria", "1",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 AVE COLONEL CHAMBONNET", "Paris", "12345",
                "Avenue Colonel Chambonnet", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 AVE. COLONEL CHAMBONNET", "Paris", "12345",
                "Avenue Colonel Chambonnet", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 PETITE IMP. DES CAMPS", "Paris", "12345",
                "Petite Impasse Des Camps", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 IMP. AV. DE L AUBAREDE", "Paris", "12345",
                "Impasse Avenue De L Aubarede", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 AV. COLONEL CHAMBONNET", "Paris", "12345",
                "Avenue Colonel Chambonnet", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 IMP DE L AV DE LA FORET DE BORD", "Paris", "12345",
                "Imp De L Av De La Foret De Bord", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 IMP DE L AV CHARLES DE GAULLE", "Paris", "12345",
                "Imp De L Av Charles De Gaulle", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 LIEU DIT LA BOULEVARDERIE", "Paris", "12345",
                "Lieu Dit La Boulevarderie", "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BOUL   .  DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BOUL DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BLVD DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BLVD  . DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BLD DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BLD   . DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis GRAND BLD DE SUPER CANNES", "Paris", "12345",
                "Grand Boulevard De Super Cannes", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis TRAVERSE DU BD DE L ESTEREL", "Paris", "12345",
                "Traverse Du Bd De L Esterel", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis ALLEE DU GERP", "Paris", "12345", "Allee Du Gerp", "76 Bis",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis RUE TRES R. P. L J DEHON ", "Paris", "12345",
                "Rue Tres Reverend Pere L J Dehon", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis RUE TRES RP. L J DEHON ", "Paris", "12345",
                "Rue Tres Reverend Pere L J Dehon", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis RUE TRES R.P L J DEHON ", "Paris", "12345",
                "Rue Tres Reverend Pere L J Dehon", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis RUE TRES RP L J DEHON ", "Paris", "12345",
                "Rue Tres Reverend Pere L J Dehon", "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "76, bis avenue Gambetta ", "Paris", "12345", "Avenue Gambetta",
                "76 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3 Rue Des Edelweiss ÉtaGE 2, Appt 9", "Paris", "12345",
                "Rue Des Edelweiss ,", "3", "ÉtaGE 2 Appt 9"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "15 Rue De L Eglise - Bat A", "Paris", "12345", "Rue De L Eglise -",
                "15", "Bat A"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 Descente de la Hall Aux Poissons", "Paris", "12345",
                "Descente De La Hall Aux Poissons", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 Impasse du bat de Boeuf", "Paris", "12345",
                "Impasse Du Bat De Boeuf", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 rue du 16 juin 1940", "Paris", "12345", "Rue Du 16 Juin 1940", "2",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Avenue du 8 mai 1945", "Paris", "12345", "Avenue Du 8 Mai 1945",
                "1", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3 ÉtaGE 2, Apt 9 Rue Des Edelweiss", "Paris", "12345",
                "Rue Des Edelweiss", "3", "ÉtaGE 2 Apt 9"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3 ÉtaGE 2, Appt 9 Rue Des Edelweiss", "Paris", "12345",
                "Rue Des Edelweiss", "3", "ÉtaGE 2 Appt 9"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3 Étage 2, Appt 9 Rue Des Edelweiss", "Paris", "12345",
                "Rue Des Edelweiss", "3", "Étage 2 Appt 9"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3/appartement A Rue Des Edelweiss", "Paris", "12345",
                "Rue Des Edelweiss", "3", "appartement A"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3/appartement 278 Rue Des Edelweiss", "Paris", "12345",
                "Rue Des Edelweiss", "3", "appartement 278"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3 Appartement 278 Rue Des Edelweiss", "Paris", "12345",
                "Rue Des Edelweiss", "3", "Appartement 278"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "4/hall 49 Rue De Crimee", "Paris", "12345", "Rue De Crimee", "4",
                "hall 49"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "3 Appt 278 Rue Des Edelweiss", "Paris", "12345", "Rue Des Edelweiss",
                "3", "Appt 278"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "03 Rue Des Edelweiss Appt 278", "Paris", "12345",
                "Rue Des Edelweiss", "3", "Appt 278"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "216 Bat. C Route De Bellet", "Paris", "12345", "Route De Bellet",
                "216", "Bat. C"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "216 Bat. 2a Route De Bellet", "Paris", "12345", "Route De Bellet",
                "216", "Bat. 2a"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "216//////$%^$%/bat. 2a Route De Bellet", "Paris", "12345",
                "Route De Bellet", "216", "bat. 2a"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1-15, quai Frédéric Mistral", "Paris", "12345",
                "Quai Frédéric Mistral", "1-15", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "15 ter rue Vaugirard", "Paris", "12345", "Rue Vaugirard", "15 Ter",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "15 Quinquies rue Vaugirard", "Paris", "12345", "Rue Vaugirard",
                "15 Quinquies", null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "15b rue Noé", "Paris", "12345", "Rue Noé", "15b", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "98bis boulevard Sebastopol", "Paris", "12345",
                "Boulevard Sebastopol", "98 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "25 rue du 14 Juillet ", "Paris", "12345", "Rue Du 14 Juillet", "25",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 bis rue du 11 Novembre", "Paris", "12345", "Rue Du 11 Novembre",
                "1 Bis", null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "12, Bd des Arceaux", "Paris", "12345", "Bd Des Arceaux", "12", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "33-35 Bd des Arceaux", "Paris", "12345", "Bd Des Arceaux", "33-35",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "10 bis rue des rosiers ", "Paris", "12345", "Rue Des Rosiers",
                "10 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1      quater rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1 Quater", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 b   bis rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1b Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 B   bis rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1b Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 / B   bis rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1b Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1quater rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1 Quater", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1QuAtEr rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1 Quater", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 $%^@#$^   bis rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1$%^@#$^bis rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1 Bis", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 $%^@#$^   ter rue Chambiges", "Paris", "12345", "Rue Chambiges",
                "1 Ter", null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "8 rue Chambiges", "Paris", "12345", "Rue Chambiges", "8", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "8. rue Chambiges", "Paris", "12345", "Rue Chambiges", "8", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "10\\00\\1 rue Chambiges", "Paris", "12345", "Rue Chambiges", "10/1",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "10\\00\\1. rue Chambiges", "Paris", "12345", "Rue Chambiges", "10/1",
                null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1=-/A rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1-A rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "00//001 rue Chambiges", "Paris", "12345", "Rue Chambiges", "1", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1/0/0/00 rue Chambiges", "Paris", "12345", "Rue Chambiges", "1", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1    bis rue Chambiges", "Paris", "12345", "Rue Chambiges", "1 Bis",
                null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1bis rue Chambiges", "Paris", "12345", "Rue Chambiges", "1 Bis", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1bIs rue Chambiges", "Paris", "12345", "Rue Chambiges", "1 Bis", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1biS rue Chambiges", "Paris", "12345", "Rue Chambiges", "1 Bis", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1ter rue Chambiges", "Paris", "12345", "Rue Chambiges", "1 Ter", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1    ter rue Chambiges", "Paris", "12345", "Rue Chambiges", "1 Ter",
                null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "8. rue Chambiges", "Paris", "12345", "Rue Chambiges", "8", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1a rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1A rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1/A rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1 / A rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "1 /// A rue Chambiges", "Paris", "12345", "Rue Chambiges", "1a", null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "15////// Avenue Du Général Leclerc", "Paris", "12345",
                "Avenue Du Général Leclerc", "15", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "/////15////// Avenue Du Général Leclerc", "Paris", "12345",
                "Avenue Du Général Leclerc", "15", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "////15 Avenue Du Général Leclerc", "Paris", "12345",
                "Avenue Du Général Leclerc", "15", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "4 Rue Des Quinze Fusillés", "Paris", "12345",
                "Rue Des Quinze Fusillés", "4", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "4-5 Rue Des Quinze Fusillés", "Paris", "12345",
                "Rue Des Quinze Fusillés", "4-5", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "4-5. Rue Des Quinze Fusillés", "Paris", "12345",
                "Rue Des Quinze Fusillés", "4-5", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "4/5. Rue Des Quinze Fusillés", "Paris", "12345",
                "Rue Des Quinze Fusillés", "4/5", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "12----------- Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "12", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "12#$^$#%&%^* Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "12", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "0000012 Avenue De La Costa", "Paris", "12345", "Avenue De La Costa",
                "12", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "0000012----------- Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "12", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "/////0000012----------- Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "12", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "/////0000012/// Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "12", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 / 12   /  14. Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "1/12/14", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 / / &&%// 12   --  14. Avenue De La Costa", "Paris", "12345",
                "Avenue De La Costa", "1/12/14", null
            },
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "rue Chambiges 1a", "Paris", "12345", "Rue Chambiges", "1a", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "rue Chambiges 1", "Paris", "12345", "Rue Chambiges", "1", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "rue Chambiges 1/2", "Paris", "12345", "Rue Chambiges", "1/2", null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", null, "Paris", "12345", "", null, null},
            {"PaRiS!@#   $$", "12 3SFS%$#45 ", "", "Paris", "12345", "", null, null},
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "26 Rue Jean Jaures B.P. 33", "Paris", "12345", "Rue Jean Jaures",
                "26", "B.P. 33"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "26 Rue Jean Jaures BP 33", "Paris", "12345", "Rue Jean Jaures", "26",
                "BP 33"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1300 chem  .     du gd vallon", "Paris", "12345",
                "Chemin Du Grand Vallon", "1300", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1300 chem. du gd vallon", "Paris", "12345", "Chemin Du Grand Vallon",
                "1300", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1300 chem. du gr vallon", "Paris", "12345", "Chemin Du Grand Vallon",
                "1300", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 Res Les Jardins De Denouval", "Paris", "12345",
                "Residence Les Jardins De Denouval", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 Rés Les Jardins De Denouval", "Paris", "12345",
                "Residence Les Jardins De Denouval", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 resi Les Jardins De Denouval", "Paris", "12345",
                "Residence Les Jardins De Denouval", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 rési Les Jardins De Denouval", "Paris", "12345",
                "Residence Les Jardins De Denouval", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2 Residence Les Jardins De Denouval", "Paris", "12345",
                "Residence Les Jardins De Denouval", "2", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "6 Sq Leprevost De La Moissonniere", "Paris", "12345",
                "Square Leprevost De La Moissonniere", "6", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "40 Rue De La Montagne Ste Genevieve", "Paris", "12345",
                "Rue De La Montagne Sainte Genevieve", "40", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "62 Rue Du Fbg St Denis", "Paris", "12345",
                "Rue Du Faubourg Saint Denis", "62", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "58 Fbg Sainte Anne", "Paris", "12345", "Faubourg Sainte Anne", "58",
                null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "1 Allee Des Landes apt 18 etg 5", "Paris", "12345",
                "Allee Des Landes", "1", "apt 18 etg 5"
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "2504 Rte De St Saturnin", "Paris", "12345",
                "Route De Saint Saturnin", "2504", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "952 Rd Point de la Foux", "Paris", "12345", "Rond Point De La Foux",
                "952", null
            },
            {
                "PaRiS!@#   $$", "12 3SFS%$#45 ", "59 Domaine De La Blanche Voie", "Paris", "12345",
                "Domaine De La Blanche Voie", "59", null
            }
        };
        return Arrays.asList(data);
            // @formatter: on
    }

    public FrenchAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String expectedCity, final String expectedZip, final String expectedName, final String expectedNr,
            final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.FR).city(city).zip(zip)
                                              .streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));
        // }
    }

}
