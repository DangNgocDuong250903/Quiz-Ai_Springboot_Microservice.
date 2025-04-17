import { Spin } from "antd";

const Button = ({
  title,
  className,
  iconRight,
  iconLeft,
  iconLeftStyles,
  titleStyles,
  type,
  onClick,
  disable,
  isLoading = false,
  ...rests
}) => {
  return (
    <button
      {...rests}
      disabled={disable || isLoading}
      onClick={onClick}
      type={type || "button"}
      className={`inline-flex items-center justify-center text-base relative ${className} ${
        (disable || isLoading) && "cursor-not-allowed bg-zinc-300"
      }`}
    >
      {iconLeft && <div className={`${iconLeftStyles}`}>{iconLeft}</div>}
      <span className={`${titleStyles} ${isLoading ? "invisible" : ""}`}>
        {title}
      </span>
      {isLoading && (
        <div className="absolute inset-0 flex items-center justify-center">
          <Spin />
        </div>
      )}
      {iconRight && <div className="ml-2">{iconRight}</div>}
    </button>
  );
};

export default Button;
