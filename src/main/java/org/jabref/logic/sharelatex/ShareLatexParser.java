package org.jabref.logic.sharelatex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.importer.ParseException;
import org.jabref.logic.importer.fileformat.BibtexParser;
import org.jabref.model.entry.BibEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ShareLatexParser {

    /**
     * json Data source containing all details and docs for one project
    [
    null,
    {
     "_id": "5909edaff31ff96200ef58dd",
     "name": "Test",
     "rootDoc_id": "5909edaff31ff96200ef58de",
     "rootFolder": [
        {
           "_id": "5909edaff31ff96200ef58dc",
           "name": "rootFolder",
           "folders": [],
           "fileRefs": [
              {
                 "_id": "5909edb0f31ff96200ef58e0",
                 "name": "universe.jpg"
              },
              {
                 "_id": "59118cae98ba55690073c2a0",
                 "name": "all2.ris"
              }
           ],
           "docs": [
              {
                 "_id": "5909edaff31ff96200ef58de",
                 "name": "main.tex"
              },
              {
                 "_id": "5909edb0f31ff96200ef58df",
                 "name": "references.bib"
              },
              {
                 "_id": "5911801698ba55690073c29c",
                 "name": "aaaaaaaaaaaaaa.bib"
              },
              {
                 "_id": "59368d551bd5906b0082f53a",
                 "name": "aaaaaaaaaaaaaa (copy 1).bib"
              }
           ]
        }
     ],
     "publicAccesLevel": "private",
     "dropboxEnabled": false,
     "compiler": "pdflatex",
     "description": "",
     "spellCheckLanguage": "en",
     "deletedByExternalDataSource": false,
     "deletedDocs": [],
     "members": [
        {
           "_id": "5912e195a303b468002eaad0",
           "first_name": "jim",
           "last_name": "",
           "email": "jim@example.com",
           "privileges": "readAndWrite",
           "signUpDate": "2017-05-10T09:47:01.325Z"
        }
     ],
     "invites": [],
     "owner": {
        "_id": "5909ed80761dc10a01f7abc0",
        "first_name": "joe",
        "last_name": "",
        "email": "joe@example.com",
        "privileges": "owner",
        "signUpDate": "2017-05-03T14:47:28.665Z"
     },
     "features": {
        "trackChanges": true,
        "references": true,
        "templates": true,
        "compileGroup": "standard",
        "compileTimeout": 180,
        "github": false,
        "dropbox": true,
        "versioning": true,
        "collaborators": -1,
        "trackChangesVisible": false
     }
    },
    "owner",
    2
    ]
    
     */

    private final JsonParser parser = new JsonParser();

    public JsonArray parseFirstPartOfJson(String documentToParse) {
        String jsonToRead = documentToParse.substring(6, documentToParse.length());

        JsonArray obj = parser.parse(jsonToRead).getAsJsonArray();

        return obj;

    }

    public List<BibEntry> parseBibEntryFromJsonArray(JsonArray arr, ImportFormatPreferences prefs)
            throws ParseException {
        JsonArray stringArr = arr.get(1).getAsJsonArray();
        StringBuilder builder = new StringBuilder();
        for (JsonElement elem : stringArr) {
            builder.append(elem.getAsString());
        }

        BibtexParser parser = new BibtexParser(prefs);
        return parser.parseEntries(builder.toString());

    }

    public Map<String, String> getDatabaseWithId(String json) {
        Map<String, String> bibFileWithId = new HashMap<>();

        JsonObject obj = parseFirstPartOfJson(json).get(1).getAsJsonObject();
        JsonArray arr = obj.get("rootFolder").getAsJsonArray();

        Optional<JsonArray> docs = arr.get(0).getAsJsonObject().entrySet().stream()
                .filter(entry -> entry.getKey().equals("docs")).map(v -> v.getValue().getAsJsonArray()).findFirst();

        docs.ifPresent(jsonArray -> {
            for (JsonElement doc : jsonArray) {
                String name = doc.getAsJsonObject().get("name").getAsString();
                String id = doc.getAsJsonObject().get("_id").getAsString();

                if (name.endsWith(".bib")) {
                    bibFileWithId.put(name, id);
                }

            }
        });

        return bibFileWithId;
    }
}