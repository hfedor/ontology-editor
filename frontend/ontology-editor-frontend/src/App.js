import React from "react";
import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";

import UploadFiles from "./components/upload-files.component";
import NodeDiv from "./components/NodeDiv";

function App() {
  return (
      <div className="container" style={{ width: "600px" }}>
        <div style={{ margin: "20px" }}>
          <h3>Upload RDF file</h3>
          <h4>React upload Files</h4>
        </div>

        <UploadFiles/>
          <NodeDiv/>
      </div>
  );
}

export default App;