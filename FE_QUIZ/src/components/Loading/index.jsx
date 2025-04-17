import React from "react";

const Loading = ({ className }) => {
  return (
    <div className={`flex flex-wrap justify-center ${className}`}>
      <img className="w-8 h-8" src="/loading.svg" alt="Loading icon" />
    </div>
  );
};

export default Loading;
