        case "timetable":
          const timeRes = await getTimetables();
          setTimetables(timeRes.data || []);
          break;
        case "curriculum":
          const curricRes = await getCurriculums();
          setCurriculums(curricRes.data || []);
          break;
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [activeSection]);

  return (
    <div>
      <button onClick={() => setActiveSection("timetable")}>Timetables</button>
      <button onClick={() => setActiveSection("curriculum")}>Curriculums</button>

      {activeSection === "timetable" && (
        <TimetableSection
          timetables={timetables}
          onUpload={uploadTimetable}
          onDownload={downloadTimetable}
          onDelete={deleteTimetable}
        />
      )}
      {activeSection === "curriculum" && (
        <CurriculumSection
          curriculums={curriculums}
          onUpload={uploadCurriculum}
          onDownload={downloadCurriculum}
          onDelete={deleteCurriculum}
        />
      )}
    </div>
  );
};

export default AdminPage;
