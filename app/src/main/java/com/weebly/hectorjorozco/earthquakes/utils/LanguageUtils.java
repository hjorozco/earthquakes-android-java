package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.weebly.hectorjorozco.earthquakes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hjoro on 11/14/2016.
 */

public final class LanguageUtils {


    // Gets the two letter code of the language of the date.
    public static String getDateLocality(String date) {

        String locality = "";

        if (date.contains("enero") || date.contains("febrero") ||
                date.contains("marzo") || date.contains("abril") ||
                date.contains("mayo") || date.contains("junio") ||
                date.contains("julio") || date.contains("agosto") ||
                date.contains("septiembre") || date.contains("octubre") ||
                date.contains("noviembre") || date.contains("diciembre")) {
            locality = "es";
        }

        if (date.contains("January") || date.contains("February") ||
                date.contains("March") || date.contains("April") ||
                date.contains("May") || date.contains("June") ||
                date.contains("July") || date.contains("August") ||
                date.contains("September") || date.contains("October") ||
                date.contains("November") || date.contains("December")) {
            locality = "en";
        }

        return locality;
    }

    public static String changeDateToEnglish(String date) {
        date = date.replace("enero", "January");
        date = date.replace("febrero", "February");
        date = date.replace("marzo", "March");
        date = date.replace("abril", "April");
        date = date.replace("mayo", "May");
        date = date.replace("junio", "June");
        date = date.replace("julio", "July");
        date = date.replace("agosto", "August");
        date = date.replace("septiembre", "September");
        date = date.replace("octubre", "October");
        date = date.replace("noviembre", "November");
        date = date.replace("diciembre", "December");
        return date;
    }

    public static String changeDateToSpanish(String date) {
        date = date.replace("January", "enero");
        date = date.replace("February", "febrero");
        date = date.replace("March", "marzo");
        date = date.replace("April", "abril");
        date = date.replace("May", "mayo");
        date = date.replace("June", "junio");
        date = date.replace("July", "julio");
        date = date.replace("August", "agosto");
        date = date.replace("September", "septiembre");
        date = date.replace("October", "octubre");
        date = date.replace("November", "noviembre");
        date = date.replace("December", "diciembre");
        return date;
    }

    /**
     * Removes Spanish accents from a string.
     *
     * @param string The String with Spanish accents.
     * @return The String without Spanish accents.
     */
    public static String removeSpanishAccents(String string) {
        string = string.replace('á', 'a');
        string = string.replace('é', 'e');
        string = string.replace('í', 'i');
        string = string.replace('ó', 'o');
        string = string.replace('ú', 'u');
        string = string.replace('Á', 'A');
        string = string.replace('É', 'E');
        string = string.replace('Í', 'I');
        string = string.replace('Ó', 'O');
        string = string.replace('Ú', 'U');
        return string;
    }

    /**
     * Translates an English language primary location to a Spanish language primary location
     *
     * @param string The English language primary location
     * @return The Spanish language primary location
     */
    public static String translateToSpanish(String string) {

        // Adds a space at the beginning of the string to make sure that the parts of the string
        // to be replaced are standalone words or phrases, and not a word or phrase inside others.
        string = " " + string;

        // A
        string = string.replace("", "");
        string = string.replace(" Afghanistan", " Afganistán");
        string = string.replace(" Alamagan region", " región de Alamagan");
        string = string.replace(" Gulf of Alaska", " Golfo de Alaska");
        string = string.replace(" Aleutian Islands", " Islas Aleutianas");
        string = string.replace(" Algeria", " Argelia");
        string = string.replace(" Amatignak Island", " Isla Amatignak");
        string = string.replace(" Andreanof Islands", " Islas Andreanof");
        string = string.replace(" Anguilla", " Anguila");
        string = string.replace(" Anton Lizardo", " Antón Lizardo");
        string = string.replace(" Ascension Island region", " Región de la Isla Ascensión");
        string = string.replace(" Ascension Island", " Isla Ascención");
        string = string.replace(" South Atlantic Ocean", " Océano Atlántico Sur");
        string = string.replace(" North Atlantic Ocean", " Océano Atlántico Norte");
        string = string.replace(" Atlantic Ocean", " Océano Atlántico");
        string = string.replace(" Azores Islands region", " región de las Islas Azores");
        string = string.replace(" Azores Islands", " Islas Azores");

        // B
        string = string.replace("", "");
        string = string.replace(" Balleny Islands region", " Región de las Islas Balleny");
        string = string.replace(" Balleny Islands", " Islas Balleny");
        string = string.replace(" Banda Sea", " Mar de Banda");
        string = string.replace(" Bering Sea", " Mar de Bering");
        string = string.replace(" Biak region", " región Biak");
        string = string.replace(" The Bottom, Bonaire, Saint Eustatius and Saba", " The Bottom, " +
                "Bonaire, San Eustaquio y Saba");
        string = string.replace(" Bougainville region", " región Bougainville");
        string = string.replace(" Bouvet Island region", " región de la Isla Bouvet");
        string = string.replace(" Bouvet Island", " Isla Bouvet");
        string = string.replace(" Bonin Islands region", " región Islas Bonin");
        string = string.replace(" Bonin Islands", " Islas Bonin");
        string = string.replace(" Brazil", " Brasil");
        string = string.replace(" Bristol Island", " Isla Bristol");
        string = string.replace(" British Virgin Islands", " Islas Vírgenes Británicas");
        string = string.replace(" Burma", " Myanmar");

        // C
        string = string.replace("", "");
        string = string.replace(" Camalu", " Camalú");
        string = string.replace(" Canada", " Canadá");
        string = string.replace(" Carlsberg Ridge", " Dorsal de Carlsberg");
        string = string.replace(" Chernabura Island", " Isla Chernabura");
        string = string.replace(" Chichicapan", " Chichicápam");
        string = string.replace(" China Sea", " Mar de China");
        string = string.replace(" Chignik Lake", " el Lago Chignik");
        string = string.replace(" Chirikof Island", " Isla Chirikof");
        string = string.replace(" Cihuatlan", " Cihuatlán");
        string = string.replace(" Coalcoman", " Coalcomán");
        string = string.replace(" Circle Hot Springs Station", " Estación Circle Hot Springs");
        string = string.replace(" Democratic Republic of the Congo", " República Democrática del Congo");
        string = string.replace(" Constitucion", " Constitución");
        string = string.replace(" Crozet Islands region", " región de las Islas Crozet");
        string = string.replace(" Crozet Islands", " Islas Crozet");
        string = string.replace(" Cyprus region", " región de Chipre");
        string = string.replace(" Cyprus", " Chipre");

        // D
        string = string.replace("", "");
        string = string.replace(" Dominican Republic", " República Dominicana");

        // E
        string = string.replace("", "");
        string = string.replace(" Easter Island region", " región de la Isla de Pascua");
        string = string.replace(" Easter Island", " Isla de Pascua");
        string = string.replace(" Estacion", " Estación");

        // F
        string = string.replace("", "");
        string = string.replace(" the Fiji Islands", " las Islas Fiji");
        string = string.replace(" Fiji Islands", " Islas Fiji");
        string = string.replace(" Fiji region", " región de Fiji");
        string = string.replace(" Flores Sea", " Mar de Flores");
        string = string.replace(" France", " Francia");


        // G
        string = string.replace("", "");
        string = string.replace(" the Galapagos Islands", " las Islas Galápagos");
        string = string.replace(" South Georgia and the South Sandwich Islands region", " región de la Isla Georgia y las Islas Sandwich del Sur");
        string = string.replace(" South Georgia and the South Sandwich Islands", " Isla Georgia y las Islas Sandwich del Sur");
        string = string.replace(" South Georgia Island region", " región Sur de la Isla Georgia");
        string = string.replace(" Georgia Island", " Isla Georgia");
        string = string.replace(" Germany", " Alemania");
        string = string.replace(" Greece", " Grecia");
        string = string.replace(" Greenland Sea", " Mar de Groenlandia");
        string = string.replace(" Greenland", " Groenlandia");
        string = string.replace(" Guadeloupe region", " región Guadalupe");
        string = string.replace(" Gulf of California", " Golfo de California");

        // H
        string = string.replace("", "");
        string = string.replace(" Hawaii", " Hawai");
        string = string.replace(" Hidalgotitlan", " Hidalgotitlán");
        string = string.replace(" Huazolotitlan", " Huazolotitlán");

        // I
        string = string.replace("", "");
        string = string.replace(" Italy", " Italia");
        string = string.replace(" Izu Islands", " Islas Izu");
        string = string.replace(" South Indian Ocean", " Océano Índico Sur");
        string = string.replace(" North Indian Ocean", " Océano Índico Norte");
        string = string.replace(" North Indian Ocean", " Océano Índico");
        string = string.replace(" Southwest Indian Ridge", " Dorsal Suroeste de la India");
        string = string.replace(" Southeast Indian Ridge", " Dorsal Sureste de la India");
        string = string.replace(" southwest Indian Ridge", " Dorsal Suroeste de la India");
        string = string.replace(" southeast Indian Ridge", " Dorsal Sureste de la India");

        // J
        string = string.replace("", "");
        string = string.replace(" Sea of Japan", " Mar de Japón");
        string = string.replace(" Japan region", " región de Japón");
        string = string.replace(" Japan", " Japón");
        string = string.replace(" Java Sea", "Mar de Java");
        string = string.replace(" Jeronimo", " Jerónimo");
        string = string.replace(" Jesus Carranza", " Jesús Carranza");
        string = string.replace(" Juarez", " Juárez");


        // K
        string = string.replace("", "");
        string = string.replace(" the Kamchatka Peninsula", " la península de Kamchatka");
        string = string.replace(" Kepulauan Mentawai region", " región Kepulauan Mentawai");
        string = string.replace(" Kenai Peninsula", " Península Kenai");
        string = string.replace(" the Kermadec Islands", " las Islas Kermadec");
        string = string.replace(" Kermadec Islands region", " región de las Islas Kermadec");
        string = string.replace(" Kermadec Islands", " Islas Kermadec");
        string = string.replace(" Kiska Volcano", " Volcán Kiska");
        string = string.replace(" the Kuril Islands", " Islas Kuril");
        string = string.replace(" Kuril Islands", " Islas Kuril");
        string = string.replace(" Kyrgyzstan", " Kirguistán");

        // L
        string = string.replace("", "");
        string = string.replace(" Labrador Sea", " Mar de Labrador");
        string = string.replace(" Laptev Sea", " Mar de Láptev");
        string = string.replace(" Ligurian Sea", " Mar de Liguria");
        string = string.replace(" Little Sitkin Island", " Isla Little Sitkin");
        string = string.replace(" the Loyalty Islands", " las Islas de la Lealtad");
        string = string.replace(" Loyalty Islands", " las Islas de la Lealtad");
        string = string.replace(" Leeward Islands", " Islas Leeward");


        // M
        string = string.replace("", "");
        string = string.replace(" Macquarie Island region", " región de la Isla Macquarie");
        string = string.replace(" Macquarie Island", " Isla Macquarie");
        string = string.replace(" Maria", " María");
        string = string.replace(" Northern Mariana Islands", " Islas Marianas del Norte");
        string = string.replace(" the Mariana Islands", " las Islas Marianas");
        string = string.replace(" Mariana Islands", " Islas Marianas");
        string = string.replace(" Martinica region", " región Martinica");
        string = string.replace(" Martinique", " Martinica");
        string = string.replace(" New Mexico", " Nuevo México");
        string = string.replace(" Mexico", " México");
        string = string.replace(" mexico", " México");
        string = string.replace(" Federated States of Micronesia region", " región de Estados Federados de Micronesia");
        string = string.replace(" Federated States of Micronesia", " Estados Federados de Micronesia");
        string = string.replace(" Central Mid-Atlantic Ridge", " Dorsal Mesoatlántica Central");
        string = string.replace(" Northern Mid-Atlantic Ridge", " Norte de la Dorsal Mesoatlántica");
        string = string.replace(" northern Mid-Atlantic Ridge", " Dorsal media del Atlántico del Norte");
        string = string.replace(" Southern Mid-Atlantic Ridge", " Sur de la Dorsal Mesoatlántica");
        string = string.replace(" southern Mid-Atlantic Ridge", " Dorsal media del Atlántico del Sur");
        string = string.replace(" Morocco", " Marruecos");
        // N
        string = string.replace("", "");
        string = string.replace(" Ndoi Island region", " región de la Isla Ndoi");
        string = string.replace(" Ndoi Island", " Isla Ndoi");
        string = string.replace(" eastern New Guinea region", " región este de Nueva Guinea");
        string = string.replace(" New Guinea", " Nueva Guinea");
        string = string.replace(" New Caledonia", " Nueva Caledonia");
        string = string.replace(" New Ireland region", " región de Nueva Irlanda");
        string = string.replace(" New Ireland", " Nueva Irlanda");
        string = string.replace(" New York", " Nueva York");
        string = string.replace(" New Zealand", " Nueva Zelanda");
        string = string.replace(" North Korea", " Corea del Norte");
        string = string.replace(" Northern Territory", " Territorio Norte");
        string = string.replace(" Norwegian Sea", " Mar Noruego");


        // O
        string = string.replace("", "");
        string = string.replace(" Sea of Okhotsk", " Mar de Okhotsk");
        string = string.replace(" Old Faithful Geyser", " Géiser Old Faithful");
        string = string.replace(" Olympic Peninsula", " Península Olímpica");
        string = string.replace(" Oregon", " Oregón");
        string = string.replace(" Owen Fracture Zone region", " región de la Zona de Fractura de Owen");
        string = string.replace(" Owen Fracture Zone", " Zona de Fractura de Owen");

        // P
        string = string.replace("", "");
        string = string.replace(" Pacific-Antarctic Ridge", " Dorsal del Pacífico-Antártico ");
        string = string.replace(" Central East Pacific Rise", " Elevación Central del Pacífico del Este");
        string = string.replace(" Northern East Pacific Rise", " Norte del Dorsal del Pacífico Este");
        string = string.replace(" northern East Pacific Rise", " Norte del Dorsal del Pacífico Este");
        string = string.replace(" Southern East Pacific Rise", " Sur del Dorsal del Pacífico Oriental");
        string = string.replace(" southern East Pacific Rise", " Sur del Dorsal del Pacífico Oriental");
        string = string.replace(" central Pacific Ocean", " Océano Pacífico Central");
        string = string.replace(" Pacific Ocean", " Océano Pacífico");
        string = string.replace(" Pakistan", " Pakistán");
        string = string.replace(" Palau region", " región Palau");
        string = string.replace(" Panama", " Panamá");
        string = string.replace(" Paredon", " Paredón");
        string = string.replace(" central Peru", " el centro de Perú");
        string = string.replace(" Peru-Ecuador border region", " región de la frontera de Perú-Ecuador");
        string = string.replace(" Peru", " Perú");
        string = string.replace(" Philippines", " Filipinas");
        string = string.replace(" Poland", " Polonia");
        string = string.replace(" Port Moresby", " Puerto Moresby");
        string = string.replace(" Prince Edward Islands region", " Región de las Islas Príncipe Eduardo");
        string = string.replace(" Prince Edward Islands", " Islas Príncipe Eduardo");
        string = string.replace(" Puerto Penasco", " Puerto Peñasco");
        string = string.replace(" Puerto Rico region", " región de Puerto Rico");

        // R
        string = string.replace("", "");
        string = string.replace(" Raoul Island region", " región de la Isla Raoul");
        string = string.replace(" Raoul Island", " Isla Raoul");
        string = string.replace(" Rat Islands", " Islas Rat");
        string = string.replace(" Raton", " Ratón");
        string = string.replace(" Redoubt Volcano", " Volcán Redoubt");
        string = string.replace(" Romania", " Rumania");
        string = string.replace(" Russia region", " región de Rusia");
        string = string.replace(" Russia", " Rusia");

        // S
        string = string.replace("", "");
        string = string.replace(" Saint Helena", " Santa Helena");
        string = string.replace(" City of Saint Paul", " Ciudad de San Pablo");
        string = string.replace(" Saipan region", " región Saipan");
        string = string.replace(" South Sandwich Islands region", " región Sur de las Islas Sandwich");
        string = string.replace(" the South Sandwich Islands", " las Islas Sandwich del sur");
        string = string.replace(" South Sandwich Islands", " Islas Sandwich del sur");
        string = string.replace(" Sandwich Islands", " Islas Sandwich");
        string = string.replace(" Santa Rosalia", " Santa Rosalía");
        string = string.replace(" Scotia Sea", " mar de Escocia");
        string = string.replace(" Sebastian", " Sebastián");
        string = string.replace(" Semisopochnoi Island region", " región de la Isla Semisopochnoi");
        string = string.replace(" Semisopochnoi Island", " Isla Semisopochnoi");
        string = string.replace(" Sichuan-Yunnan border region", " región de la frontera Sichuan-Yunnan");
        string = string.replace(" Socorro Island", " Isla Socorro");
        string = string.replace(" Solomon Islands region", " región de las Islas Salomón");
        string = string.replace(" Solomon Islands", " Islas Salomón");
        string = string.replace(" South Korea", " Corea del Sur");
        string = string.replace(" Spain", " España");
        string = string.replace(" Svalbard and Jan Mayen", " Svalbard y Jan Mayen");
        string = string.replace(" Switzerland", " Suiza");

        // T
        string = string.replace("", "");
        string = string.replace(" Taiwan region", " región de Taiwan");
        string = string.replace(" Tajikistan", " Tayikistán");
        string = string.replace(" Tanaga Volcano", " Volcán Tanaga");
        string = string.replace(" Tennessee", " Tennesse");
        string = string.replace(" The Geysers", " Los Géisers");
        string = string.replace(" Timor Sea", " Mar de Timor");
        string = string.replace(" Tomatlan", " Tomatlán");
        string = string.replace(" Tristan da Cunha region", " Región Tristan da Cunha");
        string = string.replace(" Turkey", " Turquía");
        string = string.replace(" Turkmenistan-Iran border region", " Región fronteriza Turkmenistán-Irán");
        string = string.replace(" Turkmenistan", " Turkmenistán");

        // U
        string = string.replace("", "");
        string = string.replace(" Unimak Island region", " región de la Isla Unimak");
        string = string.replace(" Unimak Island", " Isla Unimak");
        string = string.replace(" U.S. Virgin Islands region", " región de las Islas Vírgenes de EE.UU");
        string = string.replace(" U.S. Virgin Islands", " Islas Vírgenes de EE.UU");
        string = string.replace(" U.S."," EE.UU.");

        // V
        string = string.replace("", "");
        string = string.replace(" Villa Comaltitlan", " Villa Comaltitlán");
        string = string.replace(" Visokoi Island region", " región de la Isla Visokoi");
        string = string.replace(" Visokoi Island", " Isla Visokoi");
        string = string.replace(" Volcano Islands", " Islas Volcano");

        // W
        string = string.replace("", "");
        string = string.replace(" West Chile Rise", " Dorsal del Oeste de Chile");
        string = string.replace(" Western Indian-Antarctic Ridge", " Dorsal India-Antártica Occidental");
        string = string.replace(" Windward Islands", " Islas Windward");

        // Y
        string = string.replace("", "");
        string = string.replace(" State of Yap", " Estado de Yap");
        string = string.replace(" Yosemite Valley", " Valle de Yosemite");

        // X
        string = string.replace("", "");
        string = string.replace(" Xalpatlahuac", " Xalpatláhuac");
        string = string.replace(" Xochihuehuetlan", " Xochihuehuetlán");

        // Z
        string = string.replace("", "");
        string = string.replace(" Zimbabwe", " Zimbabue");


        // PREFIXES
        string = string.replace("", "");
        string = string.replace(" Eastern Sea of", " Mar del Este de");
        string = string.replace(" eastern Sea of", " Mar del Este de");

        string = string.replace(" Northeast of", " Nordeste de");
        string = string.replace(" northeast of", " Nordeste de");
        string = string.replace(" Northwest of", " Noroeste de");
        string = string.replace(" northwest of", " Noroeste de");
        string = string.replace(" Southeast of", " Sureste de");
        string = string.replace(" southeast of", " Sureste de");
        string = string.replace(" Southwest of", " Suroeste de");
        string = string.replace(" southwest of", " Suroeste de");
        string = string.replace(" North of", " Norte de");
        string = string.replace(" north of", " Norte de");
        string = string.replace(" Northern", " Norte de");
        string = string.replace(" northern", " Norte de");
        string = string.replace(" South of", " Sur de");
        string = string.replace(" south of", " Sur de");
        string = string.replace(" Southern", " Sur de");
        string = string.replace(" southern", " Sur de");
        string = string.replace(" East of", " Este de");
        string = string.replace(" east of", " Este de");
        string = string.replace(" Eastern", " Este de");
        string = string.replace(" eastern", " Este de");
        string = string.replace(" West of", " Oeste de");
        string = string.replace(" west of", " Oeste de");
        string = string.replace(" Western", " Oeste de");
        string = string.replace(" western", " Oeste de");

        string = string.replace(" near ", " cerca de ");

        string = string.replace(" Off the west coast of", " Frente a la costa oeste de");
        string = string.replace(" off the west coast of", " Frente a la costa oeste de");
        string = string.replace(" Off the east coast of", " Frente a la costa este de");
        string = string.replace(" off the east coast of", " Frente a la costa este de");
        string = string.replace(" Off the coast of", " Frente a la costa de");
        string = string.replace(" off the coast of", " Frente a la costa de");
        string = string.replace(" offshore", " Cerca de la costa de");
        string = string.replace(" Offshore", " Cerca de la costa de");

        string = string.replace(" the North Island of", " la isla Norte de");
        string = string.replace(" the north Island of", " la isla Norte de");
        string = string.replace(" North Island of", " Isla Norte de");
        string = string.replace(" north Island of", " Isla Norte de");
        string = string.replace(" the South Island of", " la isla Sur de");
        string = string.replace(" the south Island of", " la isla Sur de");
        string = string.replace(" South Island of", " Isla Sur de");
        string = string.replace(" south Island of", " Isla Sur de");
        string = string.replace(" the north coast of", " la costa norte de");
        string = string.replace(" the south coast of", " la costa sur de");
        string = string.replace(" the east coast of", " la costa este de");
        string = string.replace(" the west coast of", " la costa oeste de");
        string = string.replace(" the coast of", " la costa de");

        string = string.replace(" de Norte", " Norte");
        string = string.replace(" de Sur", " Sur");

        string = string.replace(" North", " Norte de");
        string = string.replace(" South", " Sur de");
        string = string.replace(" East", " Este de");
        string = string.replace(" West", " Oeste de");


        // Removes the space at the beginning of the string
        string = string.trim();

        return string;

    }

    /**
     * Replaces all the abbreviations with the complate name, on the results from the USGS JSON
     * query.
     *
     * @param string String with abbreviation
     * @return the string with the complete name.
     */
    public static String abbreviationsToCompleteNames(String string) {

        string = " " + string;

        string = string.replace(" CA", " California");
        string = string.replace(" MX", " Mexico");
        string = string.replace(" B.C.", " Baja California");

        string = string.trim();

        return string;
    }

    /**
     * Adds the USA word at the end of locations on the United States of America.
     *
     * @param string The string with the primary location
     * @return the string with the USA word added at the end if the location was one of the states
     * in the United States of America.
     */
    public static String addUSAStringLocation(String string) {
        String[] usaStatesNames = {", Alabama", ", Alaska", ", Arizona", ", Arkansas", ", California",
                ", Colorado", ", Connecticut", ", Delaware", ", Florida", ", Georgia", ", Hawaii",
                ", Idaho", ", Illinois", ", Indiana", ", Iowa", ", Kansas", ", Kentucky", ", Louisiana",
                ", Maine", ", Maryland", ", Massachusetts", ", Michigan", ", Minnesota", ", Mississippi",
                ", Missouri", ", Montana", ", Nebraska", ", Nevada", ", New Hampshire", ", New Jersey",
                ", New Mexico", ", New York", ", North Carolina", ", North Dakota", ", Ohio", ", Oklahoma",
                ", Oregon", ", Pennsylvania", ", Puerto Rico", ", Rhode Island", ", South Carolina",
                ", South Dakota", ", Tennessee", ", Texas", ", Utah", ", Vermont", ", Virginia", ", Washington",
                ", West Virginia", ", Wisconsin", ", Wyoming"};

        int i = 0;
        boolean noExit = true;
        do {
            if (string.contains(usaStatesNames[i])) {
                string = string + ", U.S.";
                noExit = false;
            }
            i++;
        }
        while (noExit && i<50);

        return string;
    }

    /**
     * Checks if the filter process of a location is not a special case
     *
     * @param locationPrimary The primary location of the earthquake.
     * @param locationFilter  The location filter specified by the user.
     * @return true if it is a special case, false otherwise.
     */
    public static boolean locationSearchSpecialCase(String locationPrimary, String locationFilter) {
        Boolean isSpecialCase = false;
        locationFilter = locationFilter.trim();

        switch (locationFilter) {
            case "mexico":
                if (locationPrimary.contains("new mexico")) {
                    isSpecialCase = true;
                }
                break;
            case "california":
                if (locationPrimary.contains("baja california")) {
                    isSpecialCase = true;
                }
                break;
        }

        return isSpecialCase;
    }


    // Inserts a space between the number and the km
    public static String insertSpaceBeforeKm(String string) {
        String string1;
        String string2;
        int division = string.indexOf("km");
        string1 = string.substring(0, division);
        string2 = string.substring(division, string.length());

        return string1 + " " + string2;
    }

    /**
     * Splits the String location in a location offset and primary location. It translates the Strings
     * to Spanish is locality="es" and changes abreviations to complete names.
     *
     * @param location String to be split.
     * @return an array of two strings, the location offset and the primary location
     */
    public static String[] splitLocation(Context context, String location, String locality) {

        String string1;
        String string2;
        String[] splitString = new String[2];

        // If the location contains a specific indication of km
        if (location.contains("km") && (Character.isDigit(location.charAt(location.indexOf("km") - 1)))) {

            splitString[0] = location.substring(0, location.indexOf("of") + 2);
            splitString[0] = LanguageUtils.insertSpaceBeforeKm(splitString[0]);
            splitString[1] = location.substring(location.indexOf("of") + 3);
            splitString[1] = LanguageUtils.abbreviationsToCompleteNames(splitString[1]);
            splitString[1] = LanguageUtils.addUSAStringLocation(splitString[1]);

            // If the languaje of the device is Spanish
            if (locality.equals("es")) {
                string1 = splitString[0].substring(0, splitString[0].indexOf("km") + 3);
                string2 = splitString[0].substring(splitString[0].indexOf("km") + 3, splitString[0].indexOf("of"));
                string2 = string2.replace("W", "O");
                splitString[0] = string1 + "al " + string2 + "de";
                splitString[1] = LanguageUtils.translateToSpanish(splitString[1]);
            }
            // if the location does not contain specific indication of km
        } else {
            splitString[0] = context.getString(R.string.near_the);
            splitString[1] = location;
            splitString[1] = LanguageUtils.abbreviationsToCompleteNames(splitString[1]);
            splitString[1] = LanguageUtils.addUSAStringLocation(splitString[1]);

            // If the language of the device is Spanish
            if (locality.equals("es")) {
                splitString[1] = LanguageUtils.translateToSpanish(splitString[1]);
            }
        }
        return splitString;
    }

    // Changes the First letter of a String to a lowercase.
    public static String changeFirstLetterToLowercase(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    /**
     * Formats a string with the format necessary for a query to the USGS website.
     *
     * @param inputDate String Date with the format "MMMM dd, yyyy"
     * @return a String Date with the format "yyyy-MM-dd"
     */
    public static String queryFormatDate(String inputDate) {

        // Gets a String with the value of the "from Date" setting.

        long startDateOnMilliseconds = 0;
        try {

            // Two letter code of the language of the device, for example: English (en) Spanish (es)
            String locality = Resources.getSystem().getConfiguration().locale.toString().substring(0, 2);

            // Two letter code of the language of the date.
            String dateLocality = LanguageUtils.getDateLocality(inputDate);
            // Checks if the date is in the same language as the actual language of the device,
            // if not, then changes the date to the correct language.
            if (!locality.equals(dateLocality)) {
                if (dateLocality.equals("es")) {
                    inputDate = LanguageUtils.changeDateToEnglish(inputDate);
                }
                if (dateLocality.equals("en")) {
                    inputDate = LanguageUtils.changeDateToSpanish(inputDate);
                }
            }

            Date date = new SimpleDateFormat("MMMM dd, yyyy").parse(inputDate);
            startDateOnMilliseconds = date.getTime();
        } catch (ParseException e) {
            Log.e("log", e.getMessage(), e);
        }

        // Return the date in the format necessary for a query to the USGS
        return new SimpleDateFormat("yyyy-MM-dd").format(startDateOnMilliseconds);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted time string (i.e. "4:30 PM") from a Date object.
     */
    public static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}
