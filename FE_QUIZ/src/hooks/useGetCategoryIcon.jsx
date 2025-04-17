import {
  Beaker,
  BookOpen,
  BookOpenCheck,
  Brain,
  Calculator,
  Code,
  Globe,
  History,
  Music,
} from "lucide-react";

export const useGetCategoryIcon = (name) => {
  switch (name) {
    case "Lịch Sử":
      return <History className="h-12 w-12 text-amber-600" />;
    case "Toán Học":
      return <Calculator className="h-12 w-12 text-blue-600" />;
    case "Địa Lý":
      return <Globe className="h-12 w-12 text-green-600" />;
    case "Ngữ Văn":
      return <BookOpen className="h-12 w-12 text-purple-600" />;
    case "Vật Lý":
      return <Beaker className="h-12 w-12 text-red-600" />;
    case "Ứng Dụng":
      return <Code className="h-12 w-12 text-gray-600" />;
    case "Kiến Thức Tổng Quan":
      return <Brain className="h-12 w-12 text-orange-600" />;
    case "Âm Nhạc":
      return <Music className="h-12 w-12 text-pink-600" />;
    default:
      return <BookOpenCheck className="h-12 w-12 text-gray-400" />;
  }
};
