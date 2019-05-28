using CarbonCloud.Models;
using CarbonNotes.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;

namespace CarbonCloud.Providers
{
    public class NotesProvider
    {
        public static NoteGetModel GetAll(string user_id)
        {
            NoteDbContext db = new NoteDbContext();
            var notes=db.Notes.Where(x => x.UserId == user_id).ToArray();
            NoteGetModel result = new NoteGetModel();
            result.NoteName = new string[notes.Length];
            result.NoteText = new string[notes.Length];
            if (notes.Length > 0)
            {                                
                for(int i=0;i<notes.Length;i++)
                {
                    result.NoteName[i] = notes[i].NoteName;
                    result.NoteText[i] = notes[i].NoteText;
                }
            }
            return result;
        }
        public static void DeleteAll(string user_id)
        {
            NoteDbContext db = new NoteDbContext();
            var note = db.Notes.Where(x => x.UserId == user_id).ToArray();
            if (note != null)
            {
                db.Notes.RemoveRange(note);
            }
            db.SaveChanges();
        }
        public static void CreateNote(string user_id,string notename,string notetext)
        {
            NoteDbContext db = new NoteDbContext();
            db.Notes.Add(new NoteModel { UserId = user_id, NoteName = notename, NoteText = notetext });
            db.SaveChanges();
        }
        public static void DeleteNote(string id,string notename)
        {
            NoteDbContext db = new NoteDbContext();
            var note = db.Notes.First(x => x.UserId == id && x.NoteName == notename);
            if (note != null)
            {
                db.Notes.Remove(note);
            }
            db.SaveChanges();
        }
        public static void RenameNote(string user_id,string noteoldname,string notenewname)
        {
            NoteDbContext db = new NoteDbContext();
            var note = db.Notes.First(x => x.UserId == user_id && x.NoteName == noteoldname);
            note.NoteName = notenewname;
            db.SaveChanges();
        }
        public static void EditNote(string user_id,string name,string oldtext,string newtext)
        {
            NoteDbContext db = new NoteDbContext();
            var note = db.Notes.First(x => x.UserId == user_id && x.NoteName == name&&x.NoteText==oldtext);
            note.NoteText = newtext;
            db.SaveChanges();

        }
    }
}